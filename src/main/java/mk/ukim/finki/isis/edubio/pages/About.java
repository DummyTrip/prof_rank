package mk.ukim.finki.isis.edubio.pages;

import mk.ukim.finki.isis.edubio.annotations.PublicPage;
import org.apache.tapestry5.annotations.PageActivationContext;

@PublicPage
public class About
{
  @PageActivationContext
  private String learn;


  public String getLearn() {
    return learn;
  }

  public void setLearn(String learn) {
    this.learn = learn;
  }
}
