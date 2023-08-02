package com.eazybytes.eazyschool.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
/**@Table(name = "roles") -----------------> Not needed because the class name is equal to the table name*/
public class Roles extends BaseEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    private int roleId;

    /**@Column(name = "role_name") ---------> Is optional if column name and field name
     *                                      are the same after removing the '_' */
    private String roleName;

}
