package ar.edu.utn.frbb.tup;

import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaNotSupportedException;
import ar.edu.utn.frbb.tup.model.exception.TipoMonedaNotSupportedException;
import ar.edu.utn.frbb.tup.presentation.input.MenuInputProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class Application {

    public static void main(String args[]) throws TipoCuentaNotSupportedException, TipoMonedaNotSupportedException {

        ConfigurableApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(ApplicationConfig.class);

        MenuInputProcessor processor = applicationContext.getBean(MenuInputProcessor.class);
        processor.renderMenu();
    }


}
