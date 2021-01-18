package com.vspl.music.model.eo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.Map;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.vspl.music.services.DBServices;
import com.vspl.music.util.VSPLUtil;



/**
 * EO PDF Entity - saving pdf into db and write into file
 * 
 * @author Kundan kumar
 *
 */

@Entity
@Table(name = "EOPDF")
@SequenceGenerator(name = "EOPDF_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOPDF_SEQ")
@JsonIgnoreProperties("detail")
public class EOPdf extends EOObject {

	public static final String seperator = System.getProperty("file.separator");

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOPDF_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;

	@Column(name = "ENTITY_NAME")
	public String entityName;

	@Column(name = "DISPLAY_NAME")
	public String displayName;

	@Column(name = "IMAGE_URL")
	public String pdfUrl;

	@Column(name = "TYPE")
	public String type;

	@Column(name = "HEADER_PK")
	public long headerPk;

	@Column(name = "SAVE_NO")
	public int saveNo = 0;
	
	@Column(name = "CREATED_DATE")
	public Date createdDate;
	
	@Column(name = "UPDATED_DATE")
	public Date updatedDate;
	
	@Column(name="IS_ACTIVE", columnDefinition="boolean default true")
	public boolean isActive = true;

	public String pdfStorePath = System.getProperty("catalina.base") + seperator + "webapps" + seperator + "ImgData"
			+ seperator + "SMA";
	
	public EOPdf() {
	}

	public EOPdf(String entityName, String displayName, String pdfUrl, String type, long headerPk, int saveNo,
			Date createdDate, Date updatedDate, boolean isActive, String pdfStorePath) {
		super();
		this.entityName = entityName;
		this.displayName = displayName;
		this.pdfUrl = pdfUrl;
		this.type = type;
		this.headerPk = headerPk;
		this.saveNo = saveNo;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.isActive = isActive;
		this.pdfStorePath = pdfStorePath;
	}
	
	
}