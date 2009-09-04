drop table if exists csm_user;
drop table if exists catissue_password;
drop table if exists catissue_user;
drop table if exists CATISSUE_DEPARTMENT;
drop table if exists CATISSUE_INSTITUTION;
drop table if exists CATISSUE_CANCER_RESEARCH_GROUP;
drop table if exists CATISSUE_ADDRESS;

create table CATISSUE_DEPARTMENT (
   IDENTIFIER BIGINT NOT NULL AUTO_INCREMENT,
   NAME VARCHAR(50) not null unique,
   primary key (IDENTIFIER)
);
create table CATISSUE_INSTITUTION (
   IDENTIFIER BIGINT NOT NULL AUTO_INCREMENT,
   NAME VARCHAR(50) not null unique,
   primary key (IDENTIFIER)
);
create table CATISSUE_CANCER_RESEARCH_GROUP (
   IDENTIFIER BIGINT NOT NULL AUTO_INCREMENT,
   NAME VARCHAR(50) not null unique,
   primary key (IDENTIFIER)
);
create table CATISSUE_ADDRESS (
   IDENTIFIER BIGINT NOT NULL AUTO_INCREMENT,
   STREET VARCHAR(50),
   CITY VARCHAR(50),
   STATE VARCHAR(50),
   COUNTRY VARCHAR(50),
   ZIPCODE VARCHAR(30),
   PHONE_NUMBER VARCHAR(50),
   FAX_NUMBER VARCHAR(50),
   primary key (IDENTIFIER)
);

CREATE TABLE `csm_user` (                                
            `USER_ID` bigint(20) NOT NULL auto_increment,          
            `LOGIN_NAME` varchar(100) NOT NULL default '',         
            `FIRST_NAME` varchar(100) NOT NULL default '',         
            `LAST_NAME` varchar(100) NOT NULL default '',          
            `ORGANIZATION` varchar(100) default NULL,              
            `DEPARTMENT` varchar(100) default NULL,                
            `TITLE` varchar(100) default NULL,                     
            `PHONE_NUMBER` varchar(15) default NULL,               
            `PASSWORD` varchar(100) default NULL,                  
            `EMAIL_ID` varchar(100) default NULL,                  
            `START_DATE` date default NULL,                        
            `END_DATE` date default NULL,                          
            `UPDATE_DATE` date NOT NULL default '0000-00-00',      
            PRIMARY KEY  (`USER_ID`),                              
            UNIQUE KEY `UQ_LOGIN_NAME` (`LOGIN_NAME`)              
          ) ENGINE=InnoDB;

CREATE TABLE `catissue_user` (                                                                                                         
                 `IDENTIFIER` bigint(20) NOT NULL auto_increment,                                                                                     
                 `EMAIL_ADDRESS` varchar(255) default NULL,                                                                                           
                 `FIRST_NAME` varchar(255) default NULL,                                                                                              
                 `LAST_NAME` varchar(255) default NULL,                                                                                               
                 `LOGIN_NAME` varchar(255) NOT NULL,                                                                                                  
                 `START_DATE` date default NULL,                                                                                                      
                 `ACTIVITY_STATUS` varchar(50) default NULL,                                                                                          
                 `DEPARTMENT_ID` bigint(20) default NULL,                                                                                             
                 `CANCER_RESEARCH_GROUP_ID` bigint(20) default NULL,                                                                                  
                 `INSTITUTION_ID` bigint(20) default NULL,                                                                                            
                 `ADDRESS_ID` bigint(20) default NULL,                                                                                                
                 `CSM_USER_ID` bigint(20) default NULL,                                                                                               
                 `STATUS_COMMENT` text,                                                                                                               
                 `FIRST_TIME_LOGIN` bit(1) default '',                                                                                               
                 PRIMARY KEY  (`IDENTIFIER`),                                                                                                         
                 UNIQUE KEY `LOGIN_NAME` (`LOGIN_NAME`),                                                                                              
                 KEY `FKB025CFC71792AD22` (`INSTITUTION_ID`),                                                                                         
                 KEY `FKB025CFC7FFA96920` (`CANCER_RESEARCH_GROUP_ID`),                                                                               
                 KEY `FKB025CFC76CD94566` (`ADDRESS_ID`),                                                                                             
                 KEY `FKB025CFC7F30C2528` (`DEPARTMENT_ID`),                                                                                          
                 KEY `INDX_USER_LNAME` (`LAST_NAME`),                                                                                                 
                 KEY `INDX_USER_FNAME` (`FIRST_NAME`),                                                                                                
                 CONSTRAINT `FKB025CFC71792AD22` FOREIGN KEY (`INSTITUTION_ID`) REFERENCES `catissue_institution` (`IDENTIFIER`),                     
                 CONSTRAINT `FKB025CFC76CD94566` FOREIGN KEY (`ADDRESS_ID`) REFERENCES `catissue_address` (`IDENTIFIER`),                             
                 CONSTRAINT `FKB025CFC7F30C2528` FOREIGN KEY (`DEPARTMENT_ID`) REFERENCES `catissue_department` (`IDENTIFIER`),                       
                 CONSTRAINT `FKB025CFC7FFA96920` FOREIGN KEY (`CANCER_RESEARCH_GROUP_ID`) REFERENCES `catissue_cancer_research_group` (`IDENTIFIER`)  
               ) ENGINE=InnoDB;
CREATE TABLE `catissue_password` (                                                                   
                     `IDENTIFIER` bigint(20) NOT NULL auto_increment,                                                   
                     `PASSWORD` varchar(255) default NULL,                                                              
                     `UPDATE_DATE` date default NULL,                                                                   
                     `USER_ID` bigint(20) default NULL,                                                                 
                     PRIMARY KEY  (`IDENTIFIER`),                                                                       
                     KEY `FKDE1F38972206F20F` (`USER_ID`),                                                              
                     CONSTRAINT `FKDE1F38972206F20F` FOREIGN KEY (`USER_ID`) REFERENCES `catissue_user` (`IDENTIFIER`)  
                   ) ENGINE=InnoDB ;

insert into catissue_department values(1,'dept');
insert into catissue_institution  values(1,'inst');
insert into catissue_cancer_research_group  values(1,'crg');
insert into catissue_address (identifier,state,country,zipcode) values(1,null,null,null);
INSERT INTO `csm_user` VALUES (1,'admin@admin.com','Admin','Admin','','1','','','S03lnP7MVDk=','admin@admin.com','2005-08-30',NULL,'2008-01-21');
insert into catissue_user (IDENTIFIER, ACTIVITY_STATUS, CSM_USER_ID, EMAIL_ADDRESS,LOGIN_NAME,FIRST_NAME,LAST_NAME) values (1,'Active',1,'admin@admin.com','admin@admin.com','Admin','Admin');
insert into catissue_password (IDENTIFIER,PASSWORD,UPDATE_DATE,USER_ID) values ( '1','6c416f576765696c6e63316f326d3365',NULL,'1');
UPDATE CSM_USER SET LOGIN_NAME='admin@admin.com',
					DEPARTMENT=1,
					EMAIL_ID='admin@admin.com',
					PASSWORD='${first.admin.encodedPassword}'
				WHERE USER_ID=1;	

UPDATE catissue_user SET EMAIL_ADDRESS='admin@admin.com',
							LOGIN_NAME='admin@admin.com',
							DEPARTMENT_ID=1,
							INSTITUTION_ID=1,
							CANCER_RESEARCH_GROUP_ID=1,
							ADDRESS_ID=1
						WHERE 
							IDENTIFIER = 1;

							
UPDATE catissue_password set PASSWORD='xxits++sTge8j2uyHEABIQ==',
						UPDATE_DATE=now()
						WHERE 
							IDENTIFIER = 1;
commit;