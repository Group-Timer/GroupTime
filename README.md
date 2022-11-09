# GroupTime
# 1. GroupTimer coding rules

### Activity java 파일명은 사전에 정해둔 이름으로 함.
+MainActivity
+LoginActivity
+SearchIDActivity
+ChangePWActivity
+ChangeNameActivity
+RegisterActivity
+MyPageActivity
+HomeActivity
+PersonalTimeTableActivity
+GroupTimeTableActivity
+GroupToDoListActivity
+MakeGroupActivity


### 클래스명은 자바 파일명과 통일함.

### 레이아웃 xml 파일명 작성 규칙
- 맨 앞은 activity로 시작
- 단어와 단어 사이는 모두 '_'로 연결하고, 소문자로 작성
> ex. GroupTimeTableActivity.java -> activity_group_time_table.xml

### 변수 선언 시 사용 용도 주석으로 작성

### 변수 이름 작성 규칙
- 전역변수는 단어의 맨 앞글자는 대문자, 나머지는 소문자로 작성 (ex. int MyHomeAddress)
- 지역변수는 변수명 맨 앞글자는 소문자, 이외의 단어는 맨 앞글자는 대문자, 나머지는 소문자로 작성 (ex. int myHomeAddress)

### 함수 작성 시 사용 용도 주석으로 작성

### 함수 이름 작성 규칙
- 단어의 맨 앞글자는 대문자, 나머지는 소문자로 작성
- 주요 기능적 표현 단어는 단어 사이를 '_' 언더바로 연결
> ex. void Print_MyHomeAddress(), void Connect_Firebase()

### 함수 내 실행 코드에 대략적인 설명 주석으로 작성
> ex. // 파이어베이스 연결
```
connectToFirebase( ~ );

// OOO을 처리하기 위한 반복문
for( int i; ~ )
{
	~
}
```

-------------

# 2. 작업 분배

#### 권수현, 김현우
```
GroupToDoListActivity +
MakeGroupActivity + 
PersonalTimeTableActivity +
GroupTimeTableActivity +
TimeTableActivity+
TimeTableActivity+

MainActivity +
LoginActivity +
RegisterActivity +

```


#### 최재혁
```
SearchIDActivity +
ChangePWActivity +
ChangeNameActivity +
MyPageActivity +
HomeActivity +
```


