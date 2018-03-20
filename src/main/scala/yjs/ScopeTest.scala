package yjs

class ScopeTest {
  val a = 1

  def f(): Unit = {
    Scope {
      val l = Scope.lifting(a)
      val b = 2
      val z = a + b
      l + 1 + b + z
      //      List(1) match {
      //        case 1 :: Nil      => 1
      //        case a :: b :: Nil => 0
      //        case e             => e
      //      }
      //      //      ((i: Int) => {
      //        i + this.a + z
      //      })

      //      object O {
      //        val o = 0
      //        val z = b + a
      //      }
    }
  }
}
