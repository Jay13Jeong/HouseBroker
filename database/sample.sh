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
  VALUES="('title_$i', 'description_$i', $((RANDOM % 1000000 + 1)))"

  # VALUES 부분을 BATCH_INSERT_QUERY에 추가
  BATCH_INSERT_QUERY="$BATCH_INSERT_QUERY $VALUES,"

  # records
  if [ $((i % BATCH_SIZE)) -eq 0 ] || [ $i -eq $TEST_DATA_AMOUNT ]; then
    BATCH_INSERT_QUERY=${BATCH_INSERT_QUERY%,}
    FINAL_QUERY="INSERT INTO real_estate (title, description, price) VALUES $BATCH_INSERT_QUERY;"
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

