package com.wikimedia.request.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wikimedia.basedomain.BookingRequest;
import com.wikimedia.basedomain.User;
import com.wikimedia.basedomain.WikiData;
import com.wikimedia.request.entity.FakeUser;
import com.wikimedia.request.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BookingRequestService {


    @Autowired
    private UserRepository userRepository;

    public BookingRequest createBookingRequest(WikiData wikiData){
        FakeUser fakeUser = userRepository.findRandomUser();
        User user = new ObjectMapper().convertValue(fakeUser, User.class);
        BookingRequest.BookingRequestBuilder bookBuilder = BookingRequest.builder();
        bookBuilder.userId(user.getUserId());
        bookBuilder.wikiId(wikiData.getId());
        return bookBuilder.build();
    }


}
