import java.io.File

import org.cg.scala.dhc.util.XmlAnnotator
import org.scalatest.FlatSpec

import org.cg.spelstuff.Spel

/**
  * Created by ssmertnig on 4/21/17.
  */

class TestTargetSystemChecker extends FlatSpec {

  val f = new File("/home/ssmertnig/nc/hof/develop/hof-datahub/c4c-target/src/main/resources/META-INF/c4c-target-datahub-extension.xml")


  "xml" should "be patched" in {
    val src = "<a/>\n<b>\n</b>\n<c/>"

    val asXml = <a/>
      <b>
      </b>
        <c/>

    val exp = "<a ln=\"1\"/>\n<b ln=\"2\">\n</b>\n<c ln=\"4\"/>"

    var act = XmlAnnotator.annotate(src)
    assert(act.equals(exp))
  }

  "this" should "print" in {

    val f = Spel.of("T((org.apache.commons.lang3.StringUtils).isEmpty(statusCode) ? 'ACTIVE' : statusCode.toUpperCase()").filterPropertyOrFieldReference()

    val fs = f.toString()

  }

}