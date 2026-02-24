package tech.mappie

import org.jetbrains.kotlin.name.ClassId
import org.w3c.dom.Element
import tech.mappie.ir.MappieContext
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

object MappieContextFileManager {

    private const val FILE = "mappie/context.xml"

    fun load(dir: String?): MappiePersistentState =
        if (dir != null) {
            runCatching { xmlToMappiePersistentState(file(dir).readText()) }
                .getOrElse { MappiePersistentState() }
        } else {
            MappiePersistentState()
        }

    context(context: MappieContext)
    fun write(file: MappiePersistentState) {
        if (context.configuration.outputDir != null) {
            runCatching { file(context.configuration.outputDir).writeText(file.toXml()) }
        }
    }

    private fun file(dir: String) =
        File(dir).resolve(FILE).apply {
            parentFile.mkdirs()
            createNewFile()
        }

    fun MappiePersistentState.toXml(): String {
        val docFactory = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()

        val rootElement: Element = docFactory.createElement("MappiePersistentState")
        docFactory.appendChild(rootElement)

        val incrementalElement: Element = docFactory.createElement("incremental")
        for (item in incremental) {
            val itemElement = docFactory.createElement("item")
            itemElement.textContent = item.asString()
            incrementalElement.appendChild(itemElement)
        }
        rootElement.appendChild(incrementalElement)

        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")

        val writer = java.io.StringWriter()
        transformer.transform(DOMSource(docFactory), StreamResult(writer))
        return writer.toString()
    }

    fun xmlToMappiePersistentState(xml: String): MappiePersistentState {
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(xml.byteInputStream())

        doc.documentElement.normalize()
        val incrementalNodes = doc.getElementsByTagName("item")
        val list = mutableListOf<String>()
        for (i in 0 until incrementalNodes.length) {
            list += incrementalNodes.item(i).textContent
        }
        return MappiePersistentState(list.map(ClassId::fromString))
    }
}

data class MappiePersistentState(
    val incremental: List<ClassId>
) {
    constructor() : this(emptyList())
}