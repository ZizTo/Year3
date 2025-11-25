package by.bsu.contr;

import java.io.Writer;
import java.util.List;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import by.bsu.dao.StudentsDAO;

public class StudentProjectionsController implements IController {
    @Override
    public void process(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer) throws Exception {
        WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
        
        List<Object[]> data = StudentsDAO.getInstance().getStudentNamesAndDates();
        ctx.setVariable("projections", data);
        
        templateEngine.process("student/projections", ctx, writer);
    }
}
