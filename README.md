# Let-s_Go

## 交接

### **注意**

数据的交互全部采用json格式，并且使用HTTPS，数据类型严格按照json所示定义，即：

- 共有`Object, Array, string, int, double, string`这六种类型。
- 大括号`{}`表示一个Object，也就是由`key:value`组成的字典，`value`可以为六种类型中的任意一种。
- 中括号`[]`表示一个Array，数组中的元素可以为六种类型中的任意一种。
- 带引号的为string，不带引号分为以下三种:

    - true 或 false，为boolean。
    - 不为true和false，有小数点，为double。
    - 不为true和false，无小数点，为int。

- 根节点类型只能为Object或Array。

接收的数据的根节点类型实际上只可能为Object，因为一定会有一个名为`status`的`key`，其`value`为`"OK"`或者`"ERROR"`(注意全部大写)，表示本次请求是否完成。
若`status`为`"ERROR"`，则同时会有一个名为`message`的`key`，表示出错的信息，若`status`为`"OK"`，则没有`message`。


### 0.爬数据

#### url: [https://shiftlin.top/cgi-bin/Save](https://shiftlin.top/cgi-bin/Save)

#### method: POST

#### 发送数据格式：

```json
{
  "POI_num": 3,
  "category": "教育机构",
  "POIs":[
    {
      "POI_id": "1",
      "POI_name": "复旦大学",
      "latitude": 34.5,
      "longitude": 123.1,
      "type":0,
      "city":"上海市"
    },
    {
      "POI_id": "2",
      "POI_name": "中医药大学",
      "latitude": 32.5,
      "longitude": 123.34,
      "type":0,
      "city":"上海市"
    },
    {
      "POI_id": "4",
      "POI_name": "张江计算机楼",
      "latitude": 31.5,
      "longitude": 121.1,
      "type":0,
      "city":"上海市"
    }
  ]
}
```

#### 接收数据格式:

```json
{
  "status": "OK"
}
```

### 1.用户注册

#### 说明：
用户填写完userid后（要求用邮箱），前端用接口2获取验证码，用户从邮件获取验证码。
用户输入验证码后，由前端进行验证，验证通过后，则用该接口发送注册信息给后端。
若注册成功，后端返回的`status`为`OK`，并附带一个`token`(`token`的说明见登录接口)，前端可以直接登录。

#### url: [https://shiftlin.top/cgi-bin/Register](https://shiftlin.top/cgi-bin/Register)

#### method: POST

#### 发送数据格式：

```json
{
  "userid":"lshzy137@163.com", //小于等于32字节
  "password":"123456",
  "nickname":"lsh", //小于等于32字节
  "gender": 1, //0:保密(未知) 1:男 2:女
  "Tel": "15202345235" //可选 小于等于15字节
}
```

#### 接收数据格式:

```json
{
  "status": "OK",
  "token": "03ab32de7e56556b66b2fdb2df0f17da88d3774alshzy137@163.com1512827249" //小于等于128字节
}
```


### 2.获取验证码

#### 说明：
该接口可用于
+ 注册时获取验证码，对应`type`参数为0。
  若userid已经存在，则返回`status`为`ERROR`。
+ 密码找回时获取验证码，对应`type`参数为1。
  若userid不存在，则返回`status`为`ERROR`。

#### url: [https://shiftlin.top/cgi-bin/Verify.py](https://shiftlin.top/cgi-bin/Verify.py)

#### method: POST

#### 发送数据格式：

```json
{
  "userid":"lshzy137@163.com",
  "type": 0
}
```

#### 接收数据格式:

```json
{
  "status": "OK",
  "code":"1243454" //小于等于10位
}

{
  "status": "ERROR",
  "message": "用户名已经存在！"
}
```


### 3.登录

#### 说明：
登录成功后，后端会返回一个小于等于128位的`token`，以后前端调用其他接口均需要把这个`token`传给后端，用于验证身份。

#### url: [https://shiftlin.top/cgi-bin/Login](https://shiftlin.top/cgi-bin/Login)

#### method: POST

#### 发送数据格式：

```json
{
  "userid":"lshzy137@163.com",
  "password":"123456"
}
```

#### 接收数据格式:

```json
{
  "status": "OK",
  "token": "1015292bbf6baa2f0641d520e75377d2fe073123lshzy137@163.com1513578455" //小于等于128位
}

{
  "status": "ERROR",
  "message": "用户名或密码错误！"
}
```


### 4.获取帐号信息或更新个人信息

#### 说明：
若只发送了`userid`和`token`，则相当于向服务器获取帐号信息。 
若还有其他参数，则服务器会更新这些信息，然后返回新的个人信息，可用于修改密码。

#### url: [https://shiftlin.top/cgi-bin/Account](https://shiftlin.top/cgi-bin/Account)

#### method: POST

#### 发送数据格式：

```json
{
  "userid":"lshzy137@163.com",
  "token":"1015292bbf6baa2f0641d520e75377d2fe073123lshzy137@163.com1513578455"
  "nickname":"zwx"
}
```

#### 接收数据格式:

```json
{
  "status" : "OK",
  "userid":"lshzy137@163.com", //小于等于32字节
  "nickname":"zwx", //小于等于32字节
  "gender": 1, //0:保密(未知) 1:男 2:女
  "Tel": "15202345235" //可选 小于等于15字节
  "tags": 3, //3=(000...0011) 一个64位整数，对应位表示是否选择了该标签，如3表示选择了0号和1号标签; 0=(0...0)表示没有选择标签。
}
```


### 5.获取附近POI

#### url: [https://shiftlin.top/cgi-bin/Search](https://shiftlin.top/cgi-bin/Search)

#### method: POST

#### 发送数据格式： 

```json
{
  "userid":"lshzy137@gmail.com",
  "latitude": 34.5,
  "longitude": 123.1,
  "token":"1015292bbf6baa2f0641d520e75377d2fe073123lshzy137@163.com1513578455"
}
```

#### 接收数据格式:

```json
{
  "status" : "OK",
  "POI_num": 3,
  "POIs":[
    {
      "POI_id": "1",
      "POI_name": "Fudan University",
      "category":"学校",
      "latitude": 34.5,
      "longitude": 123.1,
      "popularity": 3,
      "city":"上海市"
    },
    {
      "POI_id": "2",
      "POI_name": "Gaoke Garden",
      "category":"生活区",
      "latitude": 34.5,
      "longitude": 123.1,
      "popularity": 0,
      "city":"上海市"
    },
    {
      "POI_id": "4",
      "POI_name": "张江计算机楼",
      "category":"学校",
      "latitude": 34.5,
      "longitude": 123.1,
      "popularity": 1,
      "city":"上海市"
    }
  ]
}
```

### 6.获取某个POI的POST历史记录

#### url: [https://shiftlin.top/cgi-bin/History](https://shiftlin.top/cgi-bin/History)

#### method: POST

#### 发送数据格式： 

```json
{
  "user_id": "lshzy137@163.com",
  "POI_id": "123",
  "postid": 2, //前端目前已有的当前POI的最小的postid，即最早的post，0表示获取最新的post
  "token":"1015292bbf6baa2f0641d520e75377d2fe073123lshzy137@163.com1513578455"
}
```

#### 接收数据格式:

```json
{
  "status" : "OK",
  "post_num": 3,
  "posts": [
    {
      "postid":3,
      "timestamp": 1513578455,
      "text":"23333",
      "imageUrl":null,
      "like": 2,
      "dislike": 0,
      "attitude": 0 //当前用户的态度，0表示未知，1表示点赞，2表示反对
    },
    {
      "postid":2,
      "timestamp": 1513578450,
      "text":"今天天气不好啊",
      "imageUrl":null,
      "like": 2, //点赞数
      "dislike": 0, //反对数
      "attitude": 0 //当前用户的态度，0表示未知，1表示点赞，2表示反对
    },
    {
      "postid":1,
      "timestamp": 1513578440,
      "text":"今天天气好啊",
      "imageUrl":null,
      "like": 1,
      "dislike": 1,
      "attitude": 0 //当前用户的态度，0表示未知，1表示点赞，2表示反对
    }
  ]
}
```

### 7.留下足迹

#### url: [https://shiftlin.top/cgi-bin/Post](https://shiftlin.top/cgi-bin/Post)

#### method: POST

#### 发送数据格式： 

```json
{
  "POI_id": "1",
  "userid": "lshzy137@163.com",
  "latitude": 40.43535, //用户所在纬度
  "longitude": 123.454,  //用户所在经度
  "text": "Let's Go!",
  "token": "1015292bbf6baa2f0641d520e75377d2fe073123lshzy137@163.com1513578455",
  "tags": 2524242 //一个整数,long long, binary表示
}
```

#### 接收数据格式:

```json
{
  "status":"OK",
  "timestamp":1513578600
}

{
  "status":"ERROR", //可能出错，前端需要判断
  "message":"unknown"
}
```


### 8.点赞/反对/举报

#### url: [https://shiftlin.top/cgi-bin/Feedback](https://shiftlin.top/cgi-bin/Feedback)

#### method: POST

#### 发送数据格式： 

```json
{
  "user_id": "lshzy137@163.com",
  "POI_id": "123",
  "feedback" : 0,  //1:点赞，2:反对，3:举报
  "token":"1015292bbf6baa2f0641d520e75377d2fe073123lshzy137@163.com1513578455"
}
```

#### 接收数据格式:

```json
{
  "status" : "OK"
}

{
  "status":"ERROR", //可能出错，前端需要判断
  "message":"unknown"
}
```


### 9. 推荐

#### url: [https://shiftlin.top/cgi-bin/Recommend](https://shiftlin.top/cgi-bin/Recommend)

#### method: POST

#### 发送数据格式： 

```json
{
  "userid": "lshzy137@163.com",
  "latitude": 40.43535, //用户所在纬度
  "longitude": 123.454,  //用户所在经度
  "token": "1015292bbf6baa2f0641d520e75377d2fe073123lshzy137@163.com1513578455",
  "tags": 2524242 //一个整数,long long, binary表示
}
```

#### 接收数据格式:

```json
{
  "status" : "OK",
  "POI_num": 3,
  "POIs":[
    {
      "POI_id": "1",
      "POI_name": "Fudan University",
      "category":"学校",
      "latitude": 34.5,
      "longitude": 123.1,
      "popularity": 3,
      "city":"上海市"
    },
    {
      "POI_id": "2",
      "POI_name": "Gaoke Garden",
      "category":"生活区",
      "latitude": 34.5,
      "longitude": 123.1,
      "popularity": 0,
      "city":"上海市"
    },
    {
      "POI_id": "4",
      "POI_name": "张江计算机楼",
      "category":"学校",
      "latitude": 34.5,
      "longitude": 123.1,
      "popularity": 1,
      "city":"上海市"
    }
  ]
}
```

