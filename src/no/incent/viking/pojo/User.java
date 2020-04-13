package no.incent.viking.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
	private int uid;
	private String firstname;
	private String lastname;
	private String email;
	private String address;
	private String areaCode;
	private String area;
	private String telephone;
	private String password;
	private String carRegNo;
	private String yearOfBirth;
	private String country;
	private String postbox;
	private String gender;
	private String status;
	
	public User() {}
	
	public User(Parcel in) {
		readFromParcel(in);
	}
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCarRegNo() {
		return carRegNo;
	}
	public void setCarRegNo(String carRegNo) {
		this.carRegNo = carRegNo;
	}
	public String getYearOfBirth() {
		return yearOfBirth;
	}
	public void setYearOfBirth(String yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPostbox() {
		return postbox;
	}
	public void setPostbox(String postbox) {
		this.postbox = postbox;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		@Override
		public User createFromParcel(Parcel source) {
			return new User(source);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(uid);
		dest.writeString(firstname);
		dest.writeString(lastname);
		dest.writeString(email);
		dest.writeString(address);
		dest.writeString(areaCode);
		dest.writeString(area);
		dest.writeString(telephone);
		dest.writeString(password);
		dest.writeString(carRegNo);
		dest.writeString(yearOfBirth);
		dest.writeString(country);
		dest.writeString(postbox);
		dest.writeString(gender);
		dest.writeString(status);
	}
	
	public void readFromParcel(Parcel in) {
		uid = in.readInt();
		firstname = in.readString();
		lastname = in.readString();
		email = in.readString();
		address = in.readString();
		areaCode = in.readString();
		area = in.readString();
		telephone = in.readString();
		password = in.readString();
		carRegNo = in.readString();
		yearOfBirth = in.readString();
		country = in.readString();
		postbox = in.readString();
		gender = in.readString();
		status = in.readString();
	}
}
