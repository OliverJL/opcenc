DROP TABLE opcode;
DROP TABLE opcodeSet;
DROP TABLE instruction;
DROP TABLE execFunction;
DROP TABLE binaryFile;


CREATE TABLE binaryFile ( 
 id bigint NOT NULL AUTO_INCREMENT,
 createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 name varchar(100) NOT NULL,
 fullPath varchar(200) NOT NULL,
 sizeInKb int(11) NOT NULL,
 PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='binaryFile - Info regarding filesystem (name, path, size...)';

CREATE TABLE execFunction ( 
 id bigint NOT NULL AUTO_INCREMENT,
 createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 binaryFileId bigint NOT NULL, 
 functionName varchar(200) NOT NULL,
 PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='binaryFile - Info regarding filesystem (name, path, size...)';

ALTER TABLE execFunction ADD CONSTRAINT FK_execFunction_binaryFileId FOREIGN KEY (binaryFileId) REFERENCES binaryFile(id);


CREATE TABLE instruction ( 
 id bigint NOT NULL AUTO_INCREMENT,
 createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 execFunctionId bigint NOT NULL,
 disassemblyLine varchar(600) NOT NULL,
 PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='instruction - on line of the disassembly file';

ALTER TABLE instruction ADD CONSTRAINT FK_instruction_execFunctionId FOREIGN KEY (execFunctionId) REFERENCES execFunction(id);

CREATE TABLE opcodeSet ( 
 id bigint NOT NULL AUTO_INCREMENT,
 createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 execFunctionId bigint NOT NULL,
 compressionLevel int NULL,
 compressionName varchar(100),
 encryptionKeyLength int NULL,
 encryptionAlgorithm varchar(100),
 encryptionMode varchar(10),
 relativePathToFile varchar(500),
 fullPathToFile varchar(500), 
 PRIMARY KEY (id)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='instruction - on line of the disassembly file';

ALTER TABLE opcodeSet ADD CONSTRAINT FK_opcodeSet_execFunctionId FOREIGN KEY (execFunctionId) REFERENCES execFunction(id);

CREATE TABLE opcode ( 
 id bigint NOT NULL AUTO_INCREMENT,
 createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 opcodeSetId bigint NOT NULL,
 rawCode TINYINT UNSIGNED NOT NULL, 
 rawCodeHexString varchar(4),
 sequenceIndex INT NOT NULL,  
 PRIMARY KEY (id)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='opcode - one byte of an instruction (plain/compressed/encrypted)';

ALTER TABLE opcode ADD CONSTRAINT FK_opcode_opcodeSetId FOREIGN KEY (opcodeSetId) REFERENCES opcodeSet(id);


