#!/bin/bash

set -u

# 현재 환경 변수에 특정 값이 있는지 확인
if [ "$TEST_DATA_GEN_MODE" != "true" ]; then
    exit 1
fi
echo "TEST SCRIPT START: set to the 'TEST_DATA_GEN_MODE=true'."

# Mariadb 클라이언트 명령어
MYSQL_CMD="mariadb -u root -p$MARIADB_ROOT_PASSWORD $MARIADB_DATABASE"

while true; do
    $MYSQL_CMD -e "exit" > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        break
    fi
    sleep 1
done

BATCH_SIZE=1000  # batch size
BATCH_INSERT_QUERY=""
echo ""
progress=-1
for ((i=1; i<=$TEST_DATA_AMOUNT; i++)); do
  # 각 레코드에 대한 VALUES 부분 생성
  # VALUES="('image_$i', 'image2_$i', 'image3_$i', 'image4_$i', 'image5_$i', 'image6_$i', 'image7_$i', 'image8_$i', 'image9_$i', 'image10_$i', 'title_$i', 'description_$i', $((RANDOM % 1000000 + 1)), $((RANDOM % 2)), 'relay_type_$i', 'location_$i', $((RANDOM % 1000 + 1)), 'transaction_type_$i', 'availability_date_$i', 'approval_date_$i', $((RANDOM % 100 + 1)), 'direction_$i', $((RANDOM % 1000 + 1)), $((RANDOM % 1000 + 1)), $(awk -v min=0 -v max=90 -v scale=1000 'BEGIN{srand(); print int(min+rand()*scale)}'), $(awk -v min=0 -v max=180 -v scale=1000 'BEGIN{srand(); print int(min+rand()*scale)}'))"
  VALUES="('image_$i', 'image2_$i', 'image3_$i', 'image4_$i', 'image5_$i', 'image6_$i', 'image7_$i', 'image8_$i', 'image9_$i', 'image10_$i', 'title_$i', 'description_$i', $((RANDOM % 1000000 + 1)), $((RANDOM % 2)), 'relay_type_$i', 'location_$i', $((RANDOM % 1000 + 1)), 'transaction_type_$i', 'availability_date_$i', 'approval_date_$i', $((RANDOM % 100 + 1)), 'direction_$i', $((RANDOM % 1000 + 1)), $((RANDOM % 1000 + 1)), $((RANDOM % 90 + 1)), $((RANDOM % 180 + 1)))"

  # VALUES 부분을 BATCH_INSERT_QUERY에 추가
  BATCH_INSERT_QUERY="$BATCH_INSERT_QUERY $VALUES,"

  # records
  if [ $((i % BATCH_SIZE)) -eq 0 ] || [ $i -eq $TEST_DATA_AMOUNT ]; then
    BATCH_INSERT_QUERY=${BATCH_INSERT_QUERY%,}
    FINAL_QUERY="INSERT INTO real_estate (image, image2, image3, image4, image5, image6, image7, image8, image9, image10, title, description, price, soldout, relay_object_type, location, area, transaction_type, residence_availability_date, administrative_agency_approval_date, number_of_cars_parked, direction, administration_cost, administration_cost2, latitude, longitude) VALUES $BATCH_INSERT_QUERY;"
    echo $FINAL_QUERY | $MYSQL_CMD

    if [ $? -ne 0 ]; then
      echo "Error during data insertion."
      exit 1
    fi

    BATCH_INSERT_QUERY=""  # Clear batch
  fi

  nextprogress=$(($i * 100 / $TEST_DATA_AMOUNT)) 
  if [ "$progress" != "$nextprogress" ]; then
    progress=$nextprogress
    echo -e "\033[1A\033[K"
    echo -ne "\033[1A\033[K"
    echo "Progress: $progress%"
    echo -n "curr idx: $i / $TEST_DATA_AMOUNT"    
  fi
done

echo "$TEST_DATA_AMOUNT Data insertion completed."

# chmod + x /tmp/t.sh && /tmp/t.sh &
# SELECT TABLE_ROWS FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'mydb' AND TABLE_NAME = 'real_estate';