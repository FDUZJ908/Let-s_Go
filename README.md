# Let-s_Go

## 交接

### **注意**

数据的交互全部采用json格式，数据类型严格按照json所示定义，即：

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


### 1.用户注册

#### 说明：
用户填写完userid后（要求用邮箱），前端用接口2获取验证码，用户从邮件获取验证码。
用户输入验证码后，由前端进行验证，验证通过后，则用该接口发送注册信息给后端。
若注册成功，后端返回的`status`为`OK`，并附带一个`token`（`token`的说明见登录接口)，前端可以直接登录。

#### url: [https://shiftlin.top/cgi-bin/Register](https://shiftlin.top/cgi-bin/Register)

#### method: POST

#### 发送数据格式：

```json
{
  "userid":"lshzy137@163.com",
  "password":"123456",
  "nickname":"lsh",
  "gender":"男",
  "Tel": "15202345235" //可选
}
```

#### 接收数据格式:

```json
{
  "status": "OK"，
  "token": "SGSGKHNAIEJMJHGH31423R" //小于等于128位
}
```

### 2.获取验证码

#### 说明：
该接口可用于
+ 注册时获取验证码，对应`type`参数为0。
  若userid已经存在，则返回`status`为`ERROR`。
+ 密码找回时获取验证码，对应`type`参数为1。
  若userid不存在，则返回`status`为`ERROR`。

#### url: [https://shiftlin.top/cgi-bin/Verify](https://shiftlin.top/cgi-bin/Verify)

#### method: POST

#### 发送数据格式：

```json
{
  "userid":"lshzy137@163.com",
  "type":"0"
}
```

#### 接收数据格式:

```json
{
  "status": "OK",
  "code":"1243454" //小于等于10位
}
```

### 3.签到

#### 说明：
获取附近poi由前端获取，服务器没有相应的poi信息，所以签到时需要将poi信息发给后端。

#### 相关函数：`checkPosition()`

#### url: [https://shiftlin.top/cgi-bin/CheckIn](https://shiftlin.top/cgi-bin/CheckIn)

#### method: POST

#### 发送数据格式： 

```json
{
  POI_id: "1",
  POI_info: {
    category:"school",
    POI_name: "Fudan University",
    latitude: 40.43535, //poi所在纬度
    longitude: 123.454, //poi所在经度
    province: "上海市",
    city: "上海市",
    district: "黄浦区"
  },
  created_by_user: false,
  userid: "lshzy137@163.com",
  latitude: 40.43535, //用户所在纬度
  longitude: 123.454,  //用户所在经度
  text: "Let's Go!"
}
```

