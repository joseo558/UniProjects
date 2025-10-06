import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 * Create and validate dates and datetimes. Only contains static methods.
 *
 * @author David Maurício Ferreira, nº 202300443
 * @author José Luís Parreira de Oliveira, nº 202300558
 * @version 29/01/2024
 */
public class Dates
{
    /** Number of days in each month */
    public static final int[] monthDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    /**
     * Returns the index of the first digit in a string
     * @param input String the text in which to look for a digit
     * @return int the index of the first digit in a string. If no digits are found, returns -1.
     */
    public static int firstDigitIndex(String input){
        for(int i = 0; i<input.length(); i++){
            if(input.charAt(i) >= '0' && input.charAt(i) <= '9'){
                return i;
            }
        }
        return -1;
    }
    /**
     * Returns a number from a string that has one
     * @param input String the text from which to retrieve a number
     * @return int the number in the string. If no digits are found, returns -1.
     * @see #firstDigitIndex(String input)
     */
    public static int getNumber(String input){
        int number = firstDigitIndex(input);
        if(number != -1){number = Integer.parseInt(input);}
        return number;
    }
    // Year
    /**
     * Returns the year as an integer from a text input if it's valid
     * @param yearInput String the text in which to look for a year
     * @return int the year as an integer if it's valid. Otherwise returns -1.
     * @see #getNumber(String input)
     * @see #validYear(int year)
     */
    public static int validYear(String yearInput){
        int year = getNumber(yearInput);
        if(year != -1 && validYear(year)){
            return year;
        }else{
            return -1;
        }
    }
    /**
     * Returns true if the year supplied is valid (between 2000 and 3000, inclusive, limits can be adjusted in the future)
     * @param year int the year to validate
     * @return boolean true if the year is valid
     */
    public static boolean validYear(int year){
        boolean isValid = false;
        if(year >= 2000 && year <= 3000){isValid = true;}
        return isValid;
    }
    // Month
    /**
     * Returns the month as an integer from a text input if it's valid
     * @param monthInput String the text in which to look for a month number
     * @return int the month as an integer if it's valid. Otherwise returns -1.
     * @see #getNumber(String input)
     * @see #validMonth(int month)
     */
    public static int validMonth(String monthInput){
        int month = getNumber(monthInput);
        if(month != -1 && validMonth(month)){
            return month;
        }else{
            return -1;
        }
    }
    /**
     * Returns true if the month supplied is valid (between 1 and 12, inclusive)
     * @param month int the month to validate
     * @return boolean true if the month is valid
     */
    public static boolean validMonth(int month){
        boolean isValid = false;
        if(month >= 1 && month <= 12){isValid = true;}
        return isValid;
    }
    // Day
    /**
     * Returns the day as an integer from a text input if it's valid
     * @param dayinput String the text in which to look for a day
     * @return int the day as an integer if it's valid. Otherwise returns -1.
     * @see #getNumber(String input)
     * @see #validDay(int day)
     */
    public static int validDay(String dayInput){
        int day = getNumber(dayInput);
        if(day != -1 && validDay(day)){
            return day;
        }else{
            return -1;
        }
    }
    /**
     * Returns true if the day supplied is valid (between 1 and 31, inclusive)
     * @param day int the day to validate
     * @return boolean true if the day is valid
     */
    public static boolean validDay(int day){
        boolean isValid = false;
        if(day >= 1 && day <= 31){isValid = true;}
        return isValid;
    }
    /**
     * Returns true if the day is valid for that month and year (including checking if it's a leap year). Includes validation of the year, month and day provided.
     * @param year int the year of a date
     * @param month int the month of a date
     * @param day int the day of a date
     * @return boolean true if the day is valid for the month and year supplied
     * @see #validYear(int year)
     * @see #validMonth(int month)
     * @see #validDay(int day)
     * @see #monthDays
     */
    public static boolean isValidDayOfMonth(int year, int month, int day){
        if(validYear(year) && validMonth(month) && validDay(day)){
            LocalDate dateYear = LocalDate.ofYearDay(year, 1);
            boolean isLeapYear = dateYear.isLeapYear();
            int maxMonthDays = monthDays[month-1];
            if(month == 2 && isLeapYear){maxMonthDays++;}
            if(day <= maxMonthDays){return true;}
        }
        return false;
    }
    // Hours
    /**
     * Returns the hours as an integer from a text input if it's valid
     * @param hoursInput String the text in which to look for a hour number
     * @return int the hours as an integer if it's valid. Otherwise returns -1.
     * @see #getNumber(String input)
     * @see #validHours(int hours)
     */
    public static int validHours(String hoursInput){
        int hours = getNumber(hoursInput);
        if(hours != -1 && validHours(hours)){
            return hours;
        }else{
            return -1;
        }
    }
    /**
     * Returns true if the hours supplied is valid (between 1 and 23, inclusive)
     * @param hours int the hours to validate
     * @return boolean true if the hours are valid
     */
    public static boolean validHours(int hours){
        boolean isValid = false;
        if(hours >= 1 && hours <= 23){isValid = true;}
        return isValid;
    }
    // Minutes
    /**
     * Returns the minutes as an integer from a text input if it's valid
     * @param minutesInput String the text in which to look for a minutes number
     * @return int the minutes as an integer if it's valid. Otherwise returns -1.
     * @see #getNumber(String input)
     * @see #validMinutes(int minutes)
     */
    public static int validMinutes(String minutesInput){
        int minutes = getNumber(minutesInput);
        if(minutes != -1 && validMinutes(minutes)){
            return minutes;
        }else{
            return -1;
        }
    }
    /**
     * Returns true if the minutes supplied is valid (between 1 and 59, inclusive)
     * @param minutes int the minutes to validate
     * @return boolean true if the minutes are valid
     */
    public static boolean validMinutes(int minutes){
        boolean isValid = false;
        if(minutes >= 1 && minutes <= 59){isValid = true;}
        return isValid;
    }
    // Dates
    /**
     * Creates a date (LocalDate) with the integer parameters if it's valid.
     * @param year int the year of a date
     * @param month int the month of a date
     * @param day int the day of a date
     * @return LocalDate a date built with the integer parameters if its valid. Otherwise returns a null LocalDate.
     * @see #isValidDayOfMonth(int year, int month, int day)
     */
    public static LocalDate validateDate(int year, int month, int day){
        LocalDate validDate = null;
        if(isValidDayOfMonth(year, month, day)){
            validDate = LocalDate.of(year, month, day);
        }
        return validDate;
    }
    /**
     * Creates a date (LocalDate) with the text parameters if its valid.
     * @param yearInput String the text in which to look for a year
     * @param monthInput String the text in which to look for a month
     * @param dayInput String the text in which to look for a day
     * @return LocalDate a date built with the text parameters if its valid. Otherwise returns a null LocalDate.
     * @see #validYear(String yearInput)
     * @see #validMonth(String monthInput)
     * @see #validDay(String dayInput)
     * @see #isValidDayOfMonth(int year, int month, int day)
     */
    public static LocalDate validateDate(String yearInput, String monthInput, String dayInput){
        LocalDate validDate = null;
        int year = validYear(yearInput);
        int month = validMonth(monthInput);
        int day = validDay(dayInput);
        if(isValidDayOfMonth(year, month, day)){
            validDate = LocalDate.of(year, month, day);
        }
        return validDate;
    }
    /**
     * Convert a date to text for the UI
     * @param date LocalDate the date to print to the console
     * @return String the date in the format "05/04/2025"
     */
    public static String toStringDate(LocalDate date){
        String stringDate = "";
        if(date !=  null){
            String monthZero = "";
            String dayZero = "";
            int year = date.getYear();
            int month = date.getMonthValue();
            if(month<10){monthZero="0";}
            int day = date.getDayOfMonth();
            if(day<10){dayZero="0";}
            stringDate = dayZero + day + "/" + monthZero + month + "/" + year;
        }
        return stringDate;
    }
    // DateTime
    /**
     * Creates a date and time (LocalDateTime) with the integer parameters if its valid.
     * @param year int the year of a date
     * @param month int the month of a date
     * @param day int the day of a date
     * @param hours int the hours of a time
     * @param minutes int the minutes of a time
     * @return LocalDateTime a date and time built with the integer parameters if its valid. Otherwise returns a null LocalDateTime.
     * @see #isValidDayOfMonth(int year, int month, int day)
     * @see #validHours(int hours)
     * @see #validMinutes(int minutes)
     */
    public static LocalDateTime validateDateTime(int year, int month, int day, int hours, int minutes){
        LocalDateTime validDateTime = null;
        if(isValidDayOfMonth(year, month, day) && validHours(hours) && validMinutes(minutes)){
            validDateTime = LocalDateTime.of(year, month, day, hours, minutes);
        }
        return validDateTime;
    }
    /**
     * Creates a date and time (LocalDateTime) with the text parameters if its valid.
     * @param yearInput String the text in which to look for a year
     * @param monthInput String the text in which to look for a month
     * @param dayInput String the text in which to look for a day
     * @param hoursInput String the text in which to look for the hours
     * @param minutesInput String the text in which to look for the minutes
     * @return LocalDateTime a date and time built with the text parameters if its valid. Otherwise returns a null LocalDateTime.
     * @see #validYear(String yearInput)
     * @see #validMonth(String monthInput)
     * @see #validDay(String dayInput)
     * @see #validHours(String hoursInput)
     * @see #validMinutes(String minutesInput)
     * @see #isValidDayOfMonth(int year, int month, int day)
     * @see #validHours(int hours)
     * @see #validMinutes(int minutes)
     */
    public static LocalDateTime validateDateTime(String yearInput, String monthInput, String dayInput, String hoursInput, String minutesInput){
        LocalDateTime validDateTime = null;
        int year = validYear(yearInput);
        int month = validMonth(monthInput);
        int day = validDay(dayInput);
        int hours = validHours(hoursInput);
        int minutes = validMinutes(minutesInput);
        if(isValidDayOfMonth(year, month, day) && validHours(hours) && validMinutes(minutes)){
            validDateTime = LocalDateTime.of(year, month, day, hours, minutes);
        }
        return validDateTime;
    }
    /**
     * Convert a date and time to text for the UI
     * @param date LocalDateTime the date and time to print to the console
     * @return String the date in the format "05/04/2025 - 08:07"
     */
    public static String toStringDateTime(LocalDateTime dateTime){
        String stringDateTime = "";
        if(dateTime !=  null){
            String monthZero = "";
            String dayZero = "";
            String hourZero = "";
            String minuteZero = "";
            int year = dateTime.getYear();
            int month = dateTime.getMonthValue();
            if(month<10){monthZero="0";}
            int day = dateTime.getDayOfMonth();
            if(day<10){dayZero="0";}
            int hour = dateTime.getHour();
            if(hour<10){hourZero="0";}
            int minute = dateTime.getMinute();
            if(minute<10){minuteZero="0";}
            stringDateTime = dayZero + day + "/" + monthZero + month + "/" + year + " - " + hourZero + hour + ":" + minuteZero + minute;
        }
        return stringDateTime;
    }
}