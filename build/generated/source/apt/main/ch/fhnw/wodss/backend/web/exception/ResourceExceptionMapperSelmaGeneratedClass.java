// GENERATED BY S3LM4
package ch.fhnw.wodss.backend.web.exception;


@org.springframework.stereotype.Service("")
public final class ResourceExceptionMapperSelmaGeneratedClass
    implements ResourceExceptionMapper {

  @Override
  public final ResourceExceptionDto convertToDto(ResourceException inResourceException) {
    ch.fhnw.wodss.backend.web.exception.ResourceExceptionDto out = null;
    if (inResourceException != null) {
      out = new ch.fhnw.wodss.backend.web.exception.ResourceExceptionDto();
      out.setHttpStatus(inResourceException.getHttpStatus());
      out.setUserMessage(inResourceException.getUserMessage());
    }
    return out;
  }



  /**
   * Single constructor
   */
  public ResourceExceptionMapperSelmaGeneratedClass() {
  }

}
