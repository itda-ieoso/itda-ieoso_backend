BUILD_JAR=$(ls /home/ubuntu/app/build/libs/*.jar) # { } 부분부터 repository와 동일하게 경로 맞추기
JAR_NAME=$(basename $BUILD_JAR)
echo "> build 파일명: $JAR_NAME" >> /home/ubuntu/app/deploy.log

echo "> build 파일 복사" >> /home/ubuntu/app/deploy.log
DEPLOY_PATH=/home/ubuntu/app/
cp $BUILD_JAR $DEPLOY_PATH

echo "> 현재 실행중인 애플리케이션 pid 확인" >> /home/ubuntu/app/deploy.log
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >> /home/ubuntu/app/deploy.log
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo "> DEPLOY_JAR 배포"    >> /home/ubuntu/app/deploy.log
nohup java -Duser.timezone=Asia/Seoul -jar $DEPLOY_JAR >> /home/ubuntu/deploy.log 2>/home/ubuntu/app/deploy_err.log &
