package ch.fhnw.wodss.backend.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {
	
	// Match every path without a suffix (ex. /test)
	@RequestMapping({
		"/registrieren",
		"/passwortvergessen",
		"/tipps",
		"/gruppen",
		"/rangliste",
		"/profil",
		"/ergebnisse"
	})
    public String frontend() {
        return "forward:/";
    }
}
