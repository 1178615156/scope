package yjs

object Obj{
  val obj = 1
}
class ScopeTest {
  val a = 1

  def f(): Unit = {
    //封闭的作用域
    Scope {
      //允许使用lifting捕捉外部变量
      val l = Scope.lifting(a)
      val b = 2
      val z = a + b // warn use `a`
      val e = l + b // ok
      val x = Obj.obj // ok obj is static

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
