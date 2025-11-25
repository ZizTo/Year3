package by.bsu.contr;

import java.io.Writer;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import by.bsu.dao.StudentsDAO;

public class StudentsMolodechnoController implements IController {
    @Override
    public void process(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer) throws Exception {
        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
        
        ctx.setVariable("students", StudentsDAO.getInstance().getStudentsWithCourseAndCity("Molodechno"));
        ctx.setVariable("pageTitle", "Active Students from Molodechno");
        
        templateEngine.process("student/list", ctx, writer);
    }
}
