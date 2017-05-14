package org.cg.scala.dhc.util


/**
  * Created by ssmertnig on 4/22/17.
  */
object XmlAnnotator {
  val states = annotationParseState

  def annotate(xml: String): String = {
    val acc = new StringBuilder

    annotate(xml, acc, 0, 1, annotationParseState.noTag)
    acc.toString()
  }

  private def annotate(xml: String, acc: StringBuilder, position: Int, line: Int, state: String): String = {
    if (position >= xml.size)
      xml
    else {
      val cc = xml.charAt(position)

      val lookahead =
        if (position < xml.size - 1)
          xml.charAt(position + 1)
        else ""

      val (stateB, lineB, patch) = cc match {
        case '\n' => (state, line + 1, "")
        case '<' if state.equals(states.noTag) => (states.maybeOpenTag, line, "")

        case '/' if state.equals(states.maybeOpenTag) => (states.closingTag, line, "")
        case '!' if state.equals(states.maybeOpenTag) => (states.commentTag, line, "")

        case '>' if state.equals(states.openTag) => (states.noTag, line, s" ln=${'"'}${line}${'"'}") // <smthg>
        case '/' if state.equals(states.openTag) && lookahead.equals('>') => (states.noTag, line, s" ln=${'"'}${line}${'"'}") // <smthg/>

        case '>' if state.equals(states.commentTag) => (states.noTag, line, "")
        case '>' => (states.noTag, line, "")

        case _ =>
          if (state.equals(states.maybeOpenTag))
            (states.openTag, line, "")
          else
            (state, line, "")
      }

      acc.append(patch)
      acc.append(cc)

      val x = acc.toString()

      annotate(xml, acc, position + 1, lineB, stateB)
    }

  }

}
