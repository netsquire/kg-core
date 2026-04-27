package cz.netsquire.kgcore.model

import com.fasterxml.jackson.annotation.JsonProperty

case class Tag(id: Int, name: String, desc: String = "")

case class Label(id: Int, name: String, desc: String = "")

type Notion = Label

case class Node(id: Int, name: String, labels: Option[List[String]], tags: Option[List[String]], desc: String = "")

case class Link(source: Node, target: Node, relation: Notion, labels: Option[List[String]], tags: Option[List[String]])

case class KnowledgeGraph(@JsonProperty("name") var name: Option[String],
                          @JsonProperty("root") root: Option[Node],
                          @JsonProperty("links") var links: Option[List[Link]],
                          @JsonProperty("nodes") var nodes: Option[List[Node]]){
  def setName(name: String): Unit = this.name = Some(name)
}


/********** JSON structure for a structured output ********** 
 {
 "context": "string",
 "graph": [{"concept1":"link1"}, {"concept2":"link2"}, ...],
 }
 DTO for structured output from LLMs
  */

case class StructuredOutput(
                             @JsonProperty("context") val context: String, 
                             @JsonProperty("graph") val graph: List[Map[String, String]]
                           )