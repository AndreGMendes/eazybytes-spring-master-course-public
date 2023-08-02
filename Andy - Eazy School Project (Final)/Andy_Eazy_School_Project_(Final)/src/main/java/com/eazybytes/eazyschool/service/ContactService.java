package com.eazybytes.eazyschool.service;

import com.eazybytes.eazyschool.config.EazySchoolProps;
import com.eazybytes.eazyschool.constants.EazySchoolConstants;

import com.eazybytes.eazyschool.model.Contact;
import com.eazybytes.eazyschool.repository.ContactRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
// @RequestScope
// @SessionScope
// @ApplicationScope
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    EazySchoolProps eazySchoolProps;


    /**Removed because with annotation '@Slf4j'
     * the log object is created by Lombok automatically
     * 'private static Logger log = LoggerFactory.getLogger(ContactService.class);' */

    public ContactService() {
        System.out.println("Contact Service Bean Initialized");
    }



    /**
    * Save Contact Details into DB
    * @param contact
    * @return boolean
    * */

    public boolean saveMessageDetails (Contact contact) {
        boolean isSaved = false;
        contact.setStatus(EazySchoolConstants.OPEN);

        Contact savedContact = contactRepository.save(contact);
        if (null != savedContact && savedContact.getContactId() > 0) {
            isSaved = true;
        }

        return isSaved;
    }



    /*public List<Contact> findMsgsWithOpenStatus(){
        List<Contact> contactMsgs = contactRepository.findByStatus(EazySchoolConstants.OPEN);
        return contactMsgs;
    }*/


    /*public Page<Contact> findMsgsWithOpenStatus (int pageNum, String sortField, String sortDir) {

        int pageSize = 5;

        Pageable pageable = PageRequest.of (pageNum - 1,
                                            pageSize,
                                            sortDir.equals("asc") ? Sort.by(sortField).ascending()
                                                                  : Sort.by(sortField).descending());

        Page <Contact> msgPage = contactRepository.findByStatusWithQuery(EazySchoolConstants.OPEN, pageable);

        return msgPage;
    }*/


    public Page<Contact> findMsgsWithOpenStatus (int pageNum, String sortField, String sortDir) {

        int pageSize = eazySchoolProps.getPageSize();
        String successMessage = null;

        if(null != eazySchoolProps.getContact() && null != eazySchoolProps.getContact().get("pageSize")){
            /*pageSize = Integer.parseInt(eazySchoolProps.getContact().get("pageSize").trim());*/
            pageSize = Integer.parseInt(eazySchoolProps.getContact().get("pageSize"));
            successMessage = eazySchoolProps.getContact().get("successMsg");
        }

        Pageable pageable = PageRequest.of (pageNum - 1,
                pageSize,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending());

        Page <Contact> msgPage = contactRepository.findByStatusWithQuery(EazySchoolConstants.OPEN, pageable);

        return msgPage;
    }




    public boolean updateMsgStatus(int contactId){

        boolean isUpdated = false;

        int rows = contactRepository.updateMsgStatusNative(EazySchoolConstants.CLOSE, contactId);
        if (rows > 0) {
            isUpdated = true;
        }
        return isUpdated;
    }



    /*public boolean updateMsgStatus(int contactId){
        boolean isUpdated = false;


        *//** This code bellow can now be deleted because of the annotation @Query
         *  which makes it possible to sava without having to get the entity from the DB*//*
        Optional<Contact> contact = contactRepository.findById(contactId);
        contact.ifPresent(contact1 -> {
            contact1.setStatus(EazySchoolConstants.CLOSE);

        });

        Contact updatedContact = contactRepository.save(contact.get());
        if (null != updatedContact && updatedContact.getUpdatedBy() != null) {
            isUpdated = true;
        }
        return isUpdated;
    }*/
}
