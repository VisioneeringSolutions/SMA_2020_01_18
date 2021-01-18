package com.vspl.music.model.eo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
 * EO Image Entity - saving image into db and write into file
 * 
 * @author Kundan kumar
 *
 */

@Entity
@Table(name = "EOIMAGE")
@SequenceGenerator(name = "EOIMAGE_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "EOIMAGE_SEQ")
@JsonIgnoreProperties("detail")
public class EOImage extends EOObject {

	public static final String seperator = System.getProperty("file.separator");

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EOIMAGE_SEQ")
	@Column(name = "PRIMARY_KEY")
	public long primaryKey;

	@Column(name = "ENTITY_NAME")
	public String entityName;

	@Column(name = "DISPLAY_NAME")
	public String displayName;

	@Column(name = "IMAGE_URL")
	public String imageUrl;

	@Column(name = "TYPE")
	public String type;

	@Column(name = "HEADER_PK")
	public long headerPk;

	@Column(name = "SAVE_NO")
	public int saveNo = 0;
	
	@Column(name="IS_ACTIVE", columnDefinition="boolean default true")
	public boolean isActive = true;

	// public String imageStorePath = "D:\\data\\img";
	public String imageStorePath = System.getProperty("catalina.base") + seperator + "webapps" + seperator + "ImgData"
			+ seperator + "SMA";
	
	public EOImage() {
	}
	
	public EOImage(String entityName, String displayName, String imageUrl, String type, long headerPk, int saveNo,
			boolean isActive, String imageStorePath) {
		super();
		this.entityName = entityName;
		this.displayName = displayName;
		this.imageUrl = imageUrl;
		this.type = type;
		this.headerPk = headerPk;
		this.saveNo = saveNo;
		this.isActive = isActive;
		this.imageStorePath = imageStorePath;
	}

	public static EOImage createEO(Map<String, Object> map) {
		String detailStr = map.get("detail").toString();
		String detail = detailStr.substring(detailStr.indexOf(',') + 1);
		map.remove("detail");
		// map.put("detail", detail);
		String typee = filterType((String) map.get("type"));
		map.put("type", typee);
		DBServices.create(EOObject.createObject(map));
		EOImage eoImage = (EOImage) EOObject.getLatestObject("EOImage");
		eoImage.postSave(eoImage, detail);
		return eoImage;
	}

	public EOImage update(Map<String, Object> map) {
		String detailStr = map.get("detail").toString();
		String detail = detailStr.substring(detailStr.indexOf(',') + 1);
		map.remove("detail");
		String typee = map.get("type") == null ? "" : filterType((String) map.get("type"));
		map.put("type", typee);
		DBServices.update(EOObject.updateObject(this, map));
		this.postSave(this, detail);
		return this;
	}

	public void uploadImage(int saveNo, String detail) {
		try {
			if (detail != null) {
				this.imageUrl = this.imageName();
				String relativePath = "";
				if(this.entityName.equals("SMAlogo")){
					relativePath = this.imageStorePath + seperator + "Suwayama.png";
				}else{
					relativePath = this.imageStorePath + seperator + this.imageUrl;
				}
				
				//System.out.println("imageStorePath *********************************************** " + this.imageStorePath);
				File imageFIle = new File(relativePath);
				File parentDir = imageFIle.getParentFile();
				if (!parentDir.exists()) {
					parentDir.mkdirs();
				}
				// convert byte array back to BufferedImage
				String byts = new String(detail);
				// System.err.println("byts.... " + byts);
				byte[] decoded = VSPLUtil.decodeBase64(byts);
				ImageOutputStream out = new FileImageOutputStream(new File(relativePath));
				out.write(decoded);
				out.close();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String imgUrl() {
		return this.imageStorePath + this.imageUrl;
	}

	public String imageName() {
		if (type != null && "doc".equals(type)) {
			return this.entityName + "_" + this.primaryKey + "_" + this.saveNo + ".doc";
		}
		if (type != null && "docx".equals(type)) {
			return this.entityName + "_" + this.primaryKey + "_" + this.saveNo + ".docx";
		}
		if (type != null && "pdf".equals(type)) {
			return this.entityName + "_" + this.primaryKey + "_" + this.saveNo + ".pdf";
		}
		if (type != null && ("jpeg".equals(type))) {
			return this.entityName + "_" + this.primaryKey + "_" + this.saveNo + ".jpeg";
		}
		if (type != null && ("jpg".equals(type))) {
			return this.entityName + "_" + this.primaryKey + "_" + this.saveNo + ".jpg";
		}
		if (type != null && ("xlsx".equals(type))) {
			return this.entityName + "_" + this.primaryKey + "_" + this.saveNo + ".xlsx";
		} 
		if (type != null && ("xls".equals(type))) {
			return this.entityName + "_" + this.primaryKey + "_" + this.saveNo + ".xls";
		}else {
			return this.entityName + "_" + this.primaryKey + "_" + this.saveNo + ".png";

		}
	}

	public static String filterType(String type) {
		if (type != null && ("application/msword".equals(type) || "doc".equals(type))) {
			return "doc";
		}
		if (type != null && ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(type)
				|| "docx".equals(type))) {
			return "docx";
		}
		if (type != null && ("application/pdf".equals(type) || "pdf".equals(type))) {
			return "pdf";
		}
		if (type != null && ("image/jpeg".equals(type) || "image/jpg".equals(type))) {
			return "jpeg";
		}
		if (type != null && ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(type) || "xlsx".equals(type))) {
			return "xlsx";
		}
		if (type != null && ("application/vnd.ms-excel".equals(type) || "xls".equals(type))) {
			return "xls";
		}
		return "png";
	}

	public void postSave(EOImage eoImage, String detail) {
		super.postSave();
		eoImage.saveNo++;
		eoImage.uploadImage(eoImage.saveNo, detail);
		eoImage.postCreate(eoImage);
	}

	public void postSaveWithoutCommit(String detail) {
		super.postSave();
		this.saveNo++;
		this.uploadImage(this.saveNo, detail);
	}

	// encode imageFile to Base64 String
	public static String getEncodeImageDetail(String imageUrl) {
		String imageStorePath = System.getProperty("catalina.base") + seperator + "webapps" + seperator + "ImgData"
				+ seperator + "SMA";

		String relativePath = imageStorePath + seperator + imageUrl;
		File imageFIle = new File(relativePath);
		String imageString = "";
		try {
			FileInputStream fis = new FileInputStream(imageFIle);
			byte byteArray[] = new byte[(int) imageFIle.length()];
			fis.read(byteArray);
			imageString = Base64.encodeBase64String(byteArray);
			// System.out.println("imageString " + imageString);
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imageString;
	}
}
