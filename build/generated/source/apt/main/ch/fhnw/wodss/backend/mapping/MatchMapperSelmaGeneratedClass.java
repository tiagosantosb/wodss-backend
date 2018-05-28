// GENERATED BY S3LM4
package ch.fhnw.wodss.backend.mapping;


@org.springframework.stereotype.Service("")
public final class MatchMapperSelmaGeneratedClass
    implements MatchMapper {

  @Override
  public final ch.fhnw.wodss.backend.dto.MatchDto asMatchDto(ch.fhnw.wodss.backend.domain.Match inMatch) {
    ch.fhnw.wodss.backend.dto.MatchDto out = null;
    if (inMatch != null) {
      out = new ch.fhnw.wodss.backend.dto.MatchDto();
      out.setStatistics(customMapperMatchCustomMapper.statistics(inMatch.getBets()));
      out.setCategory(inMatch.getCategory());
      if (inMatch.getDatetime() != null) {
        out.setDatetime(new java.util.Date(inMatch.getDatetime().getTime()));
      }
      else {
        out.setDatetime(null);
      }
      out.setFinished(inMatch.isFinished());
      out.setId(inMatch.getId());
      out.setStadium(asStadiumDto(inMatch.getStadium()));
      out.setTeam1(asTeamDto(inMatch.getTeam1()));
      out.setTeam2(asTeamDto(inMatch.getTeam2()));
    }
    customMapperMatchInterceptor.intercept(inMatch, out);
    return out;
  }

  public final ch.fhnw.wodss.backend.dto.TeamDto asTeamDto(ch.fhnw.wodss.backend.domain.Team inTeam) {
    ch.fhnw.wodss.backend.dto.TeamDto out = null;
    if (inTeam != null) {
      out = new ch.fhnw.wodss.backend.dto.TeamDto();
      out.setCode(inTeam.getCode());
      out.setGroup(inTeam.getGroupStage());
      out.setName(inTeam.getName());
    }
    return out;
  }

  public final ch.fhnw.wodss.backend.dto.StadiumDto asStadiumDto(ch.fhnw.wodss.backend.domain.Stadium inStadium) {
    ch.fhnw.wodss.backend.dto.StadiumDto out = null;
    if (inStadium != null) {
      out = new ch.fhnw.wodss.backend.dto.StadiumDto();
      out.setCity(inStadium.getCity());
      out.setId(inStadium.getId());
      out.setName(inStadium.getName());
    }
    return out;
  }



  /**
   * This field is used for custom Mapping
   */
  @org.springframework.beans.factory.annotation.Autowired
  private MatchCustomMapper customMapperMatchCustomMapper;

  /**
   * Custom Mapper setter for customMapperMatchCustomMapper
   */
  public final void setCustomMapperMatchCustomMapper(MatchCustomMapper mapper) {
    this.customMapperMatchCustomMapper = mapper;
  }


  /**
   * This field is used for custom Mapping
   */
  @org.springframework.beans.factory.annotation.Autowired
  private MatchInterceptor customMapperMatchInterceptor;

  /**
   * Custom Mapper setter for customMapperMatchInterceptor
   */
  public final void setCustomMapperMatchInterceptor(MatchInterceptor mapper) {
    this.customMapperMatchInterceptor = mapper;
  }


  /**
   * Single constructor
   */
  public MatchMapperSelmaGeneratedClass() {
    this.customMapperMatchCustomMapper = new ch.fhnw.wodss.backend.mapping.MatchCustomMapper();
    this.customMapperMatchInterceptor = new ch.fhnw.wodss.backend.mapping.MatchInterceptor();
  }

}
