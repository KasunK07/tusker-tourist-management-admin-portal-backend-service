package com.tuskerlanka.tourismmangement.repository;

import com.tuskerlanka.tourismmangement.document.Contact;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContactRepository extends MongoRepository<Contact, String> {
}
