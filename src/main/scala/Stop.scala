class Stop(stop_name : String, stop_time : Time):
  def time() : Time =
    stop_time
  
  def name() : String =
    stop_name
  
  def earlierThan(that : Stop): Boolean = 
    this.stop_time <= that.time()
  
  override def toString(): String =
    s"${stop_name} at ${stop_time}"