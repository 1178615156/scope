package yjs


import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object Scope {

  def apply[T](t: T): T = macro ScopeImpl.impl[T]

  def lifting[T](t: T): T = t

}

class ScopeImpl(val c: blackbox.Context) {

  import c.universe._

  def waringNoIn(pos: Position, name: TermName, names: List[TermName]) = {
    if(!names.contains(name))
      c.warning(pos, s"$name is out site")
  }

  def debug(s: String) = {
    //    println(s)
  }

  type Func = PartialFunction[Tree, Option[TermName]]

  val PackageName: String = this.getClass.getPackage.getName

  def check(bodys: List[Tree], names: List[TermName] = Nil): Unit = {
    if(bodys.nonEmpty) {
      val tree = bodys.head

      val caseTerm: Func = {
        case Apply(TypeApply(Select(Select(Ident(TermName(PackageName)), TermName("Scope")), TermName("lifting")), _), value) =>
          debug(s"lift : $value")
          None

        case Ident(name: TermName)                             => waringNoIn(tree.pos, name, names); None
        case name: TermName                                    => waringNoIn(tree.pos, name, names); None
        case Select(This(className: TypeName), name: TermName) =>
          if(!names.contains(name) &&

            !tree.symbol.isStatic)
            c.warning(tree.pos, s"$name is number in ${className.toString} ;it is capture `this`")
          debug(s"closure : ${name}")
          None
      }

      val caseDef: Func = {
        case x: ValDef               => check(x.rhs :: Nil, names); Some(x.name)
        case x: DefDef               => check(x.rhs :: Nil, names); Some(x.name)
        case x: ModuleDef            => check(x.impl.body, x.name :: names); Some(x.name)
        case x: ClassDef             => check(x.impl.body, x.name.toTermName :: names); Some(x.name.toTermName)
        case Bind(name: TermName, _) => Some(name)
        case x: CaseDef              => check(x.pat :: x.guard :: x.body :: Nil, names); None
      }

      val caseOther: Func = {
        case e if e.children.isEmpty         =>
          debug(s"leaf : ${showRaw(e)}")
          None
        case e if e.children.exists(_.isDef) =>
          debug(s"exist def in children :${e}")
          check(e.children, names)
          None
        case e                               =>
          debug(s"tree : ${showRaw(e)}")
          check(e.children, names)
          None
      }
      val newNames = (caseTerm orElse caseDef orElse caseOther).apply(tree)

      check(bodys.tail, newNames.map(_ :: names).getOrElse(names))
    }
  }

  def impl[T: c.WeakTypeTag](t: c.Expr[T]): c.Expr[T] = {

    val tree = t.tree
    val bodys: List[Tree] = tree match {
      case q"{..$bodys}" => bodys.toList
    }
    check(bodys)
    t
  }
}
