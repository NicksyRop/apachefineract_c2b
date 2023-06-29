package com.mpesa.oslabs.models;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;


@Data
@Entity( name = "c2b_entries")
public class C2b_Entries {

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)
    private Long id;

    private String transactionType;

    @Column( unique = true)
    private String transactionId;

    private String mobileNumber;

    private String transactionAmount;

    private  String firstName;

    @Column( nullable = true)
    private String lastName;

    @Column( nullable = true)
    private  String middleName;

    private String billRefNumber;

    private  String orgAccountBalance;

    private  String businessShortCode;

    private String transTime;

    private Date entryTime;

    private String thirdPartyTransID;



}
