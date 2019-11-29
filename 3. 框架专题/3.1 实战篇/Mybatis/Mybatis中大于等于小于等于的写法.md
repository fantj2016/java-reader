
#### 方法一：对照表

|原符号  |     <   |     <=    |  >     |  >=  |     &   |     '    |    "|
|:-----:|:-----:|:-----:|:-----:|:-----:|:-----:|:-----:|:-----:|
|替换符号  | `&lt;` |   `&lt;= ` | `&gt;` |   `&gt;=` |  `&amp;` |  `&apos;` | `&quot;`|

示例：
```
  <select id="selectCountNotRead" resultType="java.lang.Integer">
    select count(news_id)
    from ip_news
    where user_id = #{userId,jdbcType=VARCHAR}
    and (news_status + news_ignore) &lt; 1
  </select>
```


#### 方法二：

大于等于:`<![CDATA[ >= ]]>`

小于等于:`<![CDATA[ <= ]]>`
