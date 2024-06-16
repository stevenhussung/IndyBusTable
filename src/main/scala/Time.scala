import com.github.nscala_time.time.Imports._

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

