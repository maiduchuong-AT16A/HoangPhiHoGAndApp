import pandas as pd

def is_holiday(date_str):
    # Danh sách các ngày lễ
    holidays = ['2023-11-02', '2023-11-03', '2023-11-04', '2023-12-01']

    # Chuyển đổi chuỗi ngày thành đối tượng datetime
    date_obj = pd.to_datetime(date_str, format='%d-%m-%Y')

    # Chuyển đổi thành chuỗi cùng định dạng để so sánh
    date_str_formatted = date_obj.strftime('%Y-%m-%d')

    # Kiểm tra xem ngày có phải là ngày lễ hay không
    return date_str_formatted in holidays

def checkHoliday(day,month):
    if day==30 and month==4:
        return True
    if day==1 and month ==1:
        return True
    if day==2 and month ==9 :
        return True
    if day==5 and month==1:
        return True
    return False

# Kiểm tra ngày '29-11-2023'
if checkHoliday(30,4):
    print("yes")
else:
    print("no")