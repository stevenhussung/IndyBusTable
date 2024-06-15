import org.jsoup._
import scala.jdk.CollectionConverters._
import scalatags.Text.all._
import scala.reflect.ClassTag
import com.github.nscala_time.time.Imports._

import java.io._

@main def hello(): Unit =
  println("Hello world!")

  val bus_route_page = Jsoup.connect("https://www.indygo.net/route/3-michigan-street/").get()
  val bus_stop_times = bus_route_reader(bus_route_page).sortBy(_(0)).reverse
  
  println("\n\nAnd now, some html:")
  val html_content = bus_route_to_html(bus_stop_times)

  val output_file = File("output.html")
  val file_writer = BufferedWriter(FileWriter(output_file))
  file_writer.write(html_content.toString)
  file_writer.close()
  
  /*
  //Next step: Finding the individual routes.
  //Remembering how to unpack the data structure.
  for ((weekdarity, direction), stops) <- bus_stop_times
  do
    //Will need to traverse this going across: need to get first stop_time for each stop_loc
    for (stop_name, stop_times) <- stops
    do
      for time <- stop_times
      do
        println(weekdarity.toString() + " " + direction + " " + stop_name + " " + time)
  */
  
  //Test some time comparisons
  val time_a : Time = Time("12:01pm")
  val time_b : Time = Time("12:10pm")
  val time_c : Time = Time("12:01pm")
  
  println(s"Is ${time_a} < ${time_b}? ${time_a < time_b}")
  println(s"Is ${time_a} > ${time_b}? ${time_a > time_b}")
  println(s"Is ${time_a} <= ${time_b}? ${time_a <= time_b}")
  println(s"Is ${time_a} >= ${time_b}? ${time_a >= time_b}")
  println(s"Is ${time_a} = ${time_c}? ${time_a == time_c}")
  println(s"Is ${time_a} < ${time_c}? ${time_a < time_c}")
  println(s"Is ${time_a} <= ${time_c}? ${time_a <= time_c}")
  
def bus_route_reader(bus_route_page : org.jsoup.nodes.Document) : 
  scala.collection.mutable.Buffer[
    scala.Tuple2[
      scala.Tuple2[
        java.lang.String, java.lang.String
      ], 
      scala.collection.immutable.List[
        scala.Tuple2[
          java.lang.String,
          scala.collection.mutable.Buffer[org.joda.time.DateTime]
        ]
      ]
    ]
  ]
  =
  for schedule <- bus_route_page.select("#stop_lists > *").asScala 
  yield
    var schedule_id_info = schedule.attr("id").split("-")
    var day_of_week = schedule_id_info(1).capitalize
    var direction = if schedule_id_info(2) == "1" then "Westbound" else "Eastbound"

    ((day_of_week, direction) ->
      (
      for card <- schedule.select("[class=card]").asScala
      yield
        var stop_name = card.select("div h5 button span").text
        var stop_times = card.select("li").asScala.map(_.text).map(toTime)
        (stop_name -> stop_times)
      ).toList
    )
    
def bus_route_to_html(bus_route :
  scala.collection.mutable.Buffer[
    scala.Tuple2[
      scala.Tuple2[
        java.lang.String, java.lang.String
      ], 
      scala.collection.immutable.List[
        scala.Tuple2[
          java.lang.String,
          scala.collection.mutable.Buffer[org.joda.time.DateTime]
        ]
      ]
    ]
  ]): scalatags.Text.TypedTag[String] =
  html(
    head(
      script("some script"),
      link(rel:="stylesheet",href:="./index.css")
    ),
    body
    (
      h1("Route 3: Michigan St."),
      (
      for ((weekdarity, direction), stops) <- bus_route
      yield
        div(
          h2(weekdarity ++ ": " ++ direction),
          table(cls:="styled-table",
            for (stop_name, stop_times) <- stops
            yield
              tr(
                td(stop_name), 
                (for time <- stop_times
                yield td(timeToString(time))).toList
            )
          )
        )
      ).toList
    )
  )


def getTypeAsString[T](v: T)(implicit ev: ClassTag[T]) = 
  ev.toString

class Time(time_string : String):
  def toTime : org.joda.time.DateTime =
    DateTimeFormat.forPattern("hh:mma").parseDateTime(this.time_string)
  
  def ==(that : Time): Boolean = 
    this.toTime == that.toTime
    
  def <(that : Time): Boolean =
    this.toTime < that.toTime

  def >(that : Time): Boolean =
    this.toTime > that.toTime

  def <=(that : Time): Boolean =
    this.toTime < that.toTime || this == that

  def >=(that : Time): Boolean =
    this.toTime > that.toTime || this == that

  override def toString(): String = 
    this.time_string

def toTime(s : String) =
  val first_word : String = s.split(" ")(0)
  DateTimeFormat.forPattern("hh:mma").parseDateTime(first_word)

def timeToString(t : org.joda.time.DateTime) : String =
  t.toString("hh:mm a").toLowerCase