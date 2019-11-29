>话不多说，直接代码。概念还是spark sql中的概念。


### 方式一：使用java反射来推断RDD元数据
>从文本文件拿到RDD对象->利用反射机制将RDD转换为DataFrame->注册为一个临时表->执行sql语句->再次转换为RDD->将RDD中的数据进行映射->收集数据

先创建一个实体类：Student.java
```
public class Student implements Serializable {

    private int id;
    private String name;
    private int age;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
```

```
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("RDD2DataFrameReflection").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        sc.setLogLevel("ERROR");

        SQLContext sqlContext = new SQLContext(sc);

        JavaRDD<String> lines = sc.textFile("C:\\Users\\84407\\Desktop\\student.txt");
        JavaRDD<Student> students = lines.map((Function<String, Student>) line -> {
            String[] lineSplited = line.split(",");
            Student student = new Student();
            student.setId(Integer.parseInt(lineSplited[0].trim()));
            student.setAge(Integer.parseInt(lineSplited[2].trim()));
            student.setName(lineSplited[1].trim());
            return student;
        });
        /**
         * 使用反射方式，将RDD转换为DataFrame
         * 将student.class 传入进去，其实就是用反射的方式来创建DataFrame
         * 因为Student.class本身就是反射的一个应用
         * 然后底层还得通过对Student.class进行反射，来获取其中的field
         * 这里要求，JavaBean必须实现Serializable接口，可序列化
         */
        DataFrame studentDF = sqlContext.createDataFrame(students,Student.class);
        /**
         * 拿到一个DataFrame之后，就可以将其注册为一个临时表，然后针对其中的数据执行sql语句
         */
        studentDF.registerTempTable("students");
        /**
         * 针对students 临时表执行sql语句，查询年龄小于等于18岁的学生，就是excellent
         */
        DataFrame excellentDF = sqlContext.sql("select * from students where age <= 18");
        /**
         * 将查询出来的DataFrame ，再次转换为RDD
         */
        JavaRDD<Row> excellentRDD = excellentDF.javaRDD();
        /**
         * 将RDD中的数据进行映射，映射为Student
         */
        JavaRDD<Student> excellentStudentRDD = excellentRDD.map((Function<Row, Student>) row -> {
            //row 中的数据的顺序，可能和我们期望的不一样
            Student student = new Student();
            student.setAge((Integer) row.get(0));
            student.setId(row.getInt(1));
            student.setName(row.getString(2));
            return student;
        });

        /**
         * 将数据collect回来，然后打印
         */
        List<Student> studentList = excellentStudentRDD.collect();
        for (Student stu:studentList){
            System.out.println(stu);
        }
    }
```
执行结果：
```
Student{id=1, name='FantJ', age=18}
Student{id=2, name='Fantj2', age=18}
Student{id=3, name='Fantj3', age=18}
Student{id=4, name='FantJ4', age=18}
Student{id=5, name='FantJ5', age=18}
Student{id=6, name='FantJ6', age=18}
```




### 方式二：通过编程接口来创建DF：在程序中构建元数据
>从文本中拿到JavaRDD<Row> --> 动态构造元数据 -->  将RDD转换成DF --> 注册临时表 --> 执行sql --> 收集数据

```
    public static void main(String[] args) {
        /**
         * 创建sparkConf、javaSparkContext、SqlContext
         */
        SparkConf conf = new SparkConf().setAppName("RDD2DataFrameProgrammatically").setMaster("local");

        JavaSparkContext sc = new JavaSparkContext(conf);

        SQLContext sqlContext = new SQLContext(sc);
        /**
         * 第一步：创建一个普通的，但是必须将其转换成RDD<row>的形式
         */
        JavaRDD<String> lines = sc.textFile("C:\\Users\\84407\\Desktop\\student.txt");

        JavaRDD<Row> studentRDD = lines.map(new Function<String, Row>() {
            @Override
            public Row call(String line) {
                String[] split = line.split(",");
                return RowFactory.create(Integer.valueOf(split[0]), String.valueOf(split[1]), Integer.valueOf(split[2]));
            }
        });

        /**
         * 第二步：动态构造元数据
         * 字段的数据可能都是在程序运行中才能知道其类型
         * 所以我们需要用编程的方式来动态构造元数据
         */
        List<StructField> structFields = new ArrayList<>();
        structFields.add(DataTypes.createStructField("id",DataTypes.IntegerType,true));
        structFields.add(DataTypes.createStructField("name",DataTypes.StringType,true));
        structFields.add(DataTypes.createStructField("age",DataTypes.IntegerType,true));

        StructType structType = DataTypes.createStructType(structFields);

        /**
         * 第三步：将RDD转换成DF
         */
        DataFrame studentDF = sqlContext.createDataFrame(studentRDD, structType);
        studentDF.registerTempTable("students");

        DataFrame excellentDF = sqlContext.sql("select * from students where name='FantJ'");

        List<Row> rows = excellentDF.collectAsList();
        for (Row row:rows){
            System.out.println(row);
        }


    }
```
执行结果：
```
[1,FantJ,18]
```

### 总结
方式一和方式二最大的区别在哪呢，通俗点说就是获取字段类型的手段不同。

方式一通过java反射，但是要有javabean当字段模版。
方式二通过手动编码设置line的split对象的每个数据段的类型，不用创建javabean。
