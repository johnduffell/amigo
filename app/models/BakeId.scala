package models

import cats.data.Xor
import com.gu.scanamo.DynamoFormat
import com.gu.scanamo.error.{ DynamoReadError, TypeCoercionError }

case class BakeId(recipeId: RecipeId, buildNumber: Int)

object BakeId {

  // Bake ID is stored as a single string in Dynamo, e.g. "ubuntu-wily-java8 #123"
  private val DynamoFormatRegex = """(.+) #([0-9]+)""".r

  implicit val dynamoFormat: DynamoFormat[BakeId] = {
    def fromString(s: String): Xor[DynamoReadError, BakeId] = s match {
      case DynamoFormatRegex(recipeId, buildNumber) => Xor.right(BakeId(RecipeId(recipeId), buildNumber.toInt))
      case _ => Xor.left(TypeCoercionError(new IllegalArgumentException(s"Invalid bake ID: $s")))
    }
    DynamoFormat.xmap[BakeId, String](fromString)(bakeId => s"${bakeId.recipeId.value} #${bakeId.buildNumber}")
  }

}

