// GENERATED BY S3LM4
package ch.fhnw.wodss.backend.mapping;


@org.springframework.stereotype.Service("")
public final class CommunityMapperSelmaGeneratedClass
    implements CommunityMapper {

  @Override
  public final ch.fhnw.wodss.backend.dto.CommunityPublicDto asCommunityPublicDto(ch.fhnw.wodss.backend.domain.Community inCommunity) {
    ch.fhnw.wodss.backend.dto.CommunityPublicDto out = null;
    if (inCommunity != null) {
      out = new ch.fhnw.wodss.backend.dto.CommunityPublicDto();
      out.setCreator(customMapperCommunityPublicCustomMapper.userAsLong(inCommunity.getCreator()));
      out.setId(inCommunity.getId());
      out.setJoinRequested(customMapperCommunityPublicCustomMapper.joinRequested(inCommunity.getJoinRequesters()));
      if (inCommunity.getMembers() != null) {
        java.util.HashSet<java.lang.Long> amembersTmpCollection = new java.util.HashSet<java.lang.Long>(inCommunity.getMembers().size());
        for (ch.fhnw.wodss.backend.domain.User amembersItem : inCommunity.getMembers()) {
          amembersTmpCollection.add(customMapperCommunityPublicCustomMapper.userAsLong(amembersItem));
        }
        out.setMembers(amembersTmpCollection);
      }
      else {
        out.setMembers(null);
      }
      out.setName(inCommunity.getName());
    }
    return out;
  }


  @Override
  public final ch.fhnw.wodss.backend.dto.CommunityPrivateDto asCommunityPrivateDto(ch.fhnw.wodss.backend.domain.Community inCommunity) {
    ch.fhnw.wodss.backend.dto.CommunityPrivateDto out = null;
    if (inCommunity != null) {
      out = new ch.fhnw.wodss.backend.dto.CommunityPrivateDto();
      out.setCreator(customMapperCommunityPrivateCustomMapper.userAsLong(inCommunity.getCreator()));
      out.setId(inCommunity.getId());
      if (inCommunity.getJoinRequesters() != null) {
        java.util.HashSet<java.lang.Long> ajoinrequestersTmpCollection = new java.util.HashSet<java.lang.Long>(inCommunity.getJoinRequesters().size());
        for (ch.fhnw.wodss.backend.domain.User ajoinrequestersItem : inCommunity.getJoinRequesters()) {
          ajoinrequestersTmpCollection.add(customMapperCommunityPrivateCustomMapper.userAsLong(ajoinrequestersItem));
        }
        out.setJoinRequesters(ajoinrequestersTmpCollection);
      }
      else {
        out.setJoinRequesters(null);
      }
      if (inCommunity.getMembers() != null) {
        java.util.HashSet<java.lang.Long> amembersTmpCollection = new java.util.HashSet<java.lang.Long>(inCommunity.getMembers().size());
        for (ch.fhnw.wodss.backend.domain.User amembersItem : inCommunity.getMembers()) {
          amembersTmpCollection.add(customMapperCommunityPrivateCustomMapper.userAsLong(amembersItem));
        }
        out.setMembers(amembersTmpCollection);
      }
      else {
        out.setMembers(null);
      }
      out.setName(inCommunity.getName());
    }
    return out;
  }



  /**
   * This field is used for custom Mapping
   */
  @org.springframework.beans.factory.annotation.Autowired
  private CommunityPublicCustomMapper customMapperCommunityPublicCustomMapper;

  /**
   * Custom Mapper setter for customMapperCommunityPublicCustomMapper
   */
  public final void setCustomMapperCommunityPublicCustomMapper(CommunityPublicCustomMapper mapper) {
    this.customMapperCommunityPublicCustomMapper = mapper;
  }


  /**
   * This field is used for custom Mapping
   */
  @org.springframework.beans.factory.annotation.Autowired
  private CommunityPrivateCustomMapper customMapperCommunityPrivateCustomMapper;

  /**
   * Custom Mapper setter for customMapperCommunityPrivateCustomMapper
   */
  public final void setCustomMapperCommunityPrivateCustomMapper(CommunityPrivateCustomMapper mapper) {
    this.customMapperCommunityPrivateCustomMapper = mapper;
  }


  /**
   * Single constructor
   */
  public CommunityMapperSelmaGeneratedClass() {
    this.customMapperCommunityPublicCustomMapper = new ch.fhnw.wodss.backend.mapping.CommunityPublicCustomMapper();
    this.customMapperCommunityPrivateCustomMapper = new ch.fhnw.wodss.backend.mapping.CommunityPrivateCustomMapper();
  }

}