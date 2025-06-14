userUSE AngularSQL;
CREATE TABLE `groups` (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255),
    link VARCHAR(255)
);
CREATE TABLE `options` (
    id INT PRIMARY KEY,  
    group_id VARCHAR(255),           
    label VARCHAR(255),                
    link TEXT,                         
    FOREIGN KEY (group_id) REFERENCES `groups`(id)
);

CREATE TABLE images (
    id INT PRIMARY KEY AUTO_INCREMENT,  
    group_id VARCHAR(255),             
    link TEXT,                        
    image TEXT,                   
    alt_text VARCHAR(255),            
    FOREIGN KEY (group_id) REFERENCES `groups`(id)  
);

CREATE TABLE banners (
    id INT PRIMARY KEY AUTO_INCREMENT, 
    group_id VARCHAR(255),             
    link TEXT,               
    image TEXT,                        
    alt_text VARCHAR(255),          
    FOREIGN KEY (group_id) REFERENCES `groups`(id)
);

CREATE TABLE products (
    id INT PRIMARY KEY AUTO_INCREMENT, 
    group_id VARCHAR(255),      
    name VARCHAR(255),            
    price DECIMAL(10,2),             
    image TEXT,                        
    FOREIGN KEY (group_id) REFERENCES `groups`(id) 
);

SHOW TABLES;
DESCRIBE  `groups`;
DESCRIBE banners;


