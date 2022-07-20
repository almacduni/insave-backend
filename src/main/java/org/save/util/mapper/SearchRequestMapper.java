package org.save.util.mapper;

import org.save.model.dto.search.CompanySearchRequestDto;
import org.save.model.dto.search.PlaylistSearchRequestDto;
import org.save.model.dto.search.UserSearchRequestDto;
import org.save.model.entity.common.User;
import org.save.model.entity.social.playlist.Playlist;
import org.save.model.entity.social.playlist.Ticker;
import org.springframework.stereotype.Component;

@Component
public class SearchRequestMapper {

  public CompanySearchRequestDto mapCompanyFromTicker(Ticker ticker) {
    CompanySearchRequestDto companySearchRequestDto = new CompanySearchRequestDto();
    companySearchRequestDto.setCompanyName(ticker.getCompany());
    companySearchRequestDto.setTicker(ticker.getName());
    return companySearchRequestDto;
  }

  public PlaylistSearchRequestDto mapPlaylistSearchRequestDtoFromPlaylist(Playlist playlist) {
    PlaylistSearchRequestDto playlistSearchRequestDto = new PlaylistSearchRequestDto();
    playlistSearchRequestDto.setPicture(playlist.getPicture());
    playlistSearchRequestDto.setTitle(playlist.getTitle());
    return playlistSearchRequestDto;
  }

  public UserSearchRequestDto mapUserSearchRequestDtoFromUser(User user) {
    UserSearchRequestDto userSearchRequestDto = new UserSearchRequestDto();
    userSearchRequestDto.setUsername(user.getUsername());
    userSearchRequestDto.setAvatarLink(user.getAvatarLink());
    return userSearchRequestDto;
  }
}
