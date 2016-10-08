package com.diplomska.prof_rank.pages;

import com.diplomska.prof_rank.annotations.PublicPage;
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
