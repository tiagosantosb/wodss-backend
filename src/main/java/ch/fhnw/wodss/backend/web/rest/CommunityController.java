package ch.fhnw.wodss.backend.web.rest;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.wodss.backend.businesslogic.CommunityService;
import ch.fhnw.wodss.backend.dto.CommunityChangeDto;
import ch.fhnw.wodss.backend.dto.CommunityCreateDto;
import ch.fhnw.wodss.backend.dto.CommunityDataDto;
import ch.fhnw.wodss.backend.messaging.UpdateMessage;

@RestController
@RequestMapping("/api/communities")
public class CommunityController {
	
	@Autowired
	private CommunityService communityService;
	
	@GetMapping
	public ResponseEntity<List<CommunityDataDto>> findAll() {
		return new ResponseEntity<List<CommunityDataDto>>(communityService.findAll(), HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<UpdateMessage> create(@RequestBody CommunityCreateDto community) {
		try {
			return new ResponseEntity<UpdateMessage>(communityService.create(community), HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.PRECONDITION_FAILED);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<UpdateMessage> delete(@PathVariable Long id) {
		try {
			return new ResponseEntity<UpdateMessage>(communityService.delete(id), HttpStatus.OK);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.NOT_FOUND);
		} catch (IllegalAccessException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.UNAUTHORIZED);
		}
	}

	@PostMapping("/{communityId}/joinrequests")
	public ResponseEntity<UpdateMessage> createJoinRequest(@PathVariable Long communityId) {
		try {
			return new ResponseEntity<UpdateMessage>(communityService.createJoinRequest(communityId), HttpStatus.CREATED);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.NOT_FOUND);
		} catch (IllegalStateException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/{communityId}/joinrequests/{joinRequesterId}")
	public ResponseEntity<UpdateMessage> deleteJoinRequest(@PathVariable Long communityId, @PathVariable Long joinRequesterId) {
		try {
			return new ResponseEntity<UpdateMessage>(communityService.deleteJoinRequest(communityId, joinRequesterId), HttpStatus.OK);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.NOT_FOUND);
		} catch (IllegalStateException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.BAD_REQUEST);
		} catch (IllegalAccessException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.UNAUTHORIZED);
		}
	}

	@PostMapping("/{communityId}/members")
	public ResponseEntity<UpdateMessage> acceptJoinRequest(@PathVariable Long communityId, @RequestBody CommunityChangeDto community) {
		try {
			return new ResponseEntity<UpdateMessage>(communityService.acceptJoinRequest(communityId, community), HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.PRECONDITION_FAILED);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.NOT_FOUND);
		} catch (IllegalStateException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.BAD_REQUEST);
		} catch (IllegalAccessException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.UNAUTHORIZED);
		}
	}

	@DeleteMapping("/{communityId}/members/{memberId}")
	public ResponseEntity<UpdateMessage> deleteMember(@PathVariable Long communityId, @PathVariable Long memberId) {
		try {
			return new ResponseEntity<UpdateMessage>(communityService.deleteMember(communityId, memberId), HttpStatus.OK);
		} catch (NoSuchElementException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.NOT_FOUND);
		} catch (IllegalStateException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.BAD_REQUEST);
		} catch (IllegalAccessException e) {
			return new ResponseEntity<UpdateMessage>(HttpStatus.UNAUTHORIZED);
		}
	}
}
