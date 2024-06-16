import com.github.nscala_time.time.Imports._

class Time(time_string : String):
  def toTime : org.joda.time.DateTime =
    val first_word : String = time_string.split(" ")(0)
    DateTimeFormat.forPattern("hh:mma").parseDateTime(first_word)
  
  def ==(that : Time): Boolean = 
    this.toTime == that.toTime
    
  def <(that : Time): Boolean =
    this.toTime < that.toTime

  def >(that : Time): Boolean =
    this.toTime > that.toTime

  def <=(that : Time): Boolean =
    this.toTime < that.toTime || this.toTime == that.toTime

  def >=(that : Time): Boolean =
    this.toTime > that.toTime || this.toTime == that.toTime

  override def toString(): String = 
    this.toTime.toString("H:mm").toLowerCase()

