package pureeffect.transformer.statet

sealed trait Move extends Product with Serializable

object Move {
  final case object Forward extends Move
  final case object Backward extends Move
  final case object Sleep extends  Move
}
