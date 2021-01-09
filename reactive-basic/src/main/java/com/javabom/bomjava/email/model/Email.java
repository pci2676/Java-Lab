package com.javabom.bomjava.email.model;

public class Email {
    private String subject;
    private String contents;

    public Email(String subject, String contents) {
        this.subject = subject;
        this.contents = contents;
    }

    public String getSubject() {
        return subject;
    }

    public String getContents() {
        return contents;
    }

    @Override
    public String toString() {
        return "Email{" +
                "subject='" + subject + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }
}
