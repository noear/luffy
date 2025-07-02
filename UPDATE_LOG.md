#### v1.9.5

* 优化 js 执行器，取消对 ScriptObjectMirror 的直接引用
* 修复 luffy.executor.s.python 启动会出错的问题（引用了错误的 ScriptObjectMirror）

#### v1.9.4
* 添加 AFileModel:method 字段
* solon 升为 3.3.3

#### v1.9.3
* 升级 solon 为 3.3.1
* 升级 snack3 为 3.2.133
* 升级 wood 为 1.3.18
* 升级 nashorn-core 为 15.6


#### v1.9.2
* 升级 solon 为 3.1.1
* 升级 snack3 为 3.2.129

#### v1.9.1
* 升级 solon 为 3.0.5
* 升级 snack3 为 3.2.122
* 升级 wood 为 1.3.16

#### v1.9.0
* 升级 solon 为 3.0.0

#### v1.8.0
* 升级 solon 为 2.9.2

#### v1.7.8
* 升级 solon 为 2.9.1
* 升级 snack3 为 3.2.109

#### v1.7.3
* 升级 solon 为 2.7.0
* 升级 snack3 为 3.2.88

#### v1.7.2
* 升级 solon 为 2.6.2
* 优化 扩展适配机制

#### v1.6.8
* 升级 solon 为 2.5.7
* 升级 wood 为 1.2.2
* 增加 gzip 支持

#### v1.6.7
* 升级 solon 为 2.5.4
* 升级 snack3 为 3.2.80

#### v1.6.6
* 升级 solon 为 2.4.5
* 升级 snack3 为 3.2.76
* 升级 wood 为 1.1.8

#### v1.6.3
* 升级 solon 为 2.2.16
* 升级 snack3 为 3.2.66
* 升级 wood 为 1.1.1
* 增加 XUtil::imgDelCache 接口


#### v1.6.2
* 升级 solon 为 2.2.8
* 升级 snack3 为 3.2.62
* 升级 wood 为 1.1.0

#### v1.6.1
* 升级 solon 为 2.2.6
* 升级 snack3 为 3.2.61
* 修复 DbApi::imgSet 当 note 或 tag 超长时会出错的问题 

#### v1.6.0
* 升级 solon 为 2.1.4
* 升级 snack3 为 3.2.55
* 执行器增加类型简写申明扩展支持

#### v1.5.1
* 增加 tran, tranNew, tranNot 接口。支持快捷事务控制

#### v1.5.0
* 升级 solon 为 2.0.0
* 升级 snack3 为 3.2.52
* 启动时，增加 add,rem,udp 指令的打印

#### v1.4.3
* 升级 solon 为 1.12.1
* 升级 snack3 为 3.2.50
* 升级 wood 为 1.0.7
* 添加 MediaFile 类（框架内可控方便些），取代 UploadFile

#### v1.4.2

* 升级 solon 为 1.10.13
* 升级 snack3 为 3.2.48
* 升级 wood 为 1.0.5

#### v1.4.0

* orm 框架改为 wood

#### v1.3.3

* 升级 solon 为 1.10.1
* 升级 snack3 为 3.2.34