package pl.lukasz94w.myforum.model.enums;

public enum EnumeratedCategory {
    PROGRAMMING, SPORT, ELECTRONICS, CAR, INTRODUCTION, ADVERTISEMENT, PERSONAL;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
