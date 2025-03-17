package ru.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class MySpringMvcDispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
  @Override
  protected Class<?>[] getRootConfigClasses() {
    return new Class[]{SpringConfig.class};
  }

  @Override
  protected Class<?>[] getServletConfigClasses() {
    return new Class[]{SpringConfig.class};
  }

  @Override
  public void onStartup(@NotNull ServletContext aServletContext) throws ServletException {
    super.onStartup(aServletContext);
  }

  @Override
  protected String[] getServletMappings() {
    return new String[]{"/"};
  }
}