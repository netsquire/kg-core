package cz.netsquire.kgcore.http

import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.{RequestMapping, RestController}

@Service
class GreetingService {
  def getGreeting(name: String): String = {
    s"Hello, $name! This message was processed by Scala."
  }
}

@RestController
@RequestMapping(name = "/api/greeting", produces = Array("application/json"))
class GreetingController {
  def greet(who: String = "World"): Unit = {
    val service = new GreetingService()
    println(service.getGreeting(who))
  }
}

