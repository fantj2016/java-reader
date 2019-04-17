###最常用,在键值都需要时使用。
```
Map<Integer, Integer> map = new HashMap<Integer, Integer>(); 
for (Map.Entry<Integer, Integer> entry : map.entrySet()) { 
  System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); 
}
```
###在for-each循环中遍历keys或values。
```
Map<Integer, Integer> map = new HashMap<Integer, Integer>(); 
//遍历map中的键 
for (Integer key : map.keySet()) { 
  System.out.println("Key = " + key); 
} 
//遍历map中的值 
for (Integer value : map.values()) { 
  System.out.println("Value = " + value); 
}
```
###使用Iterator遍历
```
Map<Integer, Integer> map = new HashMap<Integer, Integer>(); 
Iterator<Map.Entry<Integer, Integer>> entries = map.entrySet().iterator(); 
while (entries.hasNext()) { 
  Map.Entry<Integer, Integer> entry = entries.next(); 
  System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); 
}
```

```
Map map = new HashMap(); 
Iterator entries = map.entrySet().iterator(); 
while (entries.hasNext()) { 
  Map.Entry entry = (Map.Entry) entries.next(); 
  Integer key = (Integer)entry.getKey(); 
  Integer value = (Integer)entry.getValue(); 
  System.out.println("Key = " + key + ", Value = " + value); 
}
```
###通过键找值遍历（效率低）
```
Map<Integer, Integer> map = new HashMap<Integer, Integer>(); 
for (Integer key : map.keySet()) { 
  Integer value = map.get(key); 
  System.out.println("Key = " + key + ", Value = " + value);
```
