# scope

### 背景
 由于scala对闭包完善的支持;使得在使用spark/akka是容易捕捉到不可序列化的对象

### 方案
  提供一个封闭的作用域`Scope`;如果捕捉到了外部变量会提示`warn`

### example

```scala
class ScopeTest {
  val a = 1

  def f(): Unit = {
    Scope {
      val l = Scope.lifting(a)
      val b = 2
      val z = a + b // warn use `a`
      val e = l + b // ok

      // ok
      List(1) match {
        case 1 :: Nil      => 1 + l
        case a :: b :: Nil => 0 + l
        case e             =>
      }
      // ok
      val func = (i: Int) => i + 0


      object O {
        val o = 0
        val z = b + a // warn use `a`
      }
      O
    }
  }
}
```
