# 🌿 Cheer You 🌿
<img width="648" alt="스크린샷 2025-03-09 오후 2 21 11" src="https://github.com/user-attachments/assets/e2677dd5-8f59-4d38-b947-6fceb237ed7d" />


```
 현대인의 심적 안정을 위한 치유/상담, cheer you
```

<br/>

## 🧑‍💻 Cheer You Server Developer
| 김승진 |
| :---: | 
| <img width="250" alt="branch" src="https://github.com/user-attachments/assets/321f6779-951d-417a-91e8-6ae905ac87ae"> | 
| [Kimseungjin0529](https://github.com/Kimseungjin0529) |
| 모든 도메인 설계 <br> RestDocs 세팅 <br> CI/CD 구축 <br> AWS 서버 구축 <br> ERD 및 DB 설계 <br> Swagger 세팅 <br> 인증 / 인가 구현 (Redis) |


<br/>

### 🌳 Git Flow 전략
<img width="672" alt="branch" src="https://github.com/MOONSHOT-Team/MOONSHOT-SERVER/assets/48898994/92c073a2-4415-4911-8d80-90530b9e41cb">
<img width="672" alt="branch" src="https://github.com/MOONSHOT-Team/MOONSHOT-SERVER/assets/48898994/c349d5d3-8c5d-489c-83b3-1f302c770d1d">

<br/> 


<br/>

### 🌳 Commit Convention

```swift
[prefix] #이슈번호 - 이슈 내용 or [prefix] 커밋명 - 커밋 내용
```
```bash
[feat]: 새로운 기능 구현 및 설정
[fix]: 버그, 오류 해결, 코드 수정
[style]: 쓸모없는 코드, 주석 삭제, 의미 없는 변경
[refactor]: 전면 수정이 있을 때 사용합니다
[chore]: 그 이외의 잡일/ 버전 코드 수정, 패키지 구조 변경, 파일 이동, 파일이름 변경
```

<br/>

### 📁 Foldering
```
└── 🗂️src
    └── 🗂️main
        └── 🗂️java
            └── 🗂️com
                └── 🗂️project
                    └── 🗂️doongdoong
                        ├── 🗂️domain
                        │   ├── 🗂️analysis
                        │   │   ├── 🗂️controller
                        │   │   ├── 🗂️dto
                        │   │   │   └── 🗂️response
                        │   │   ├── 🗂️exception
                        │   │   ├── 🗂️model
                        │   │   ├── 🗂️repository
                        │   │   │   └── 🗂️querydls
                        │   │   └── 🗂️service
                        │   ├── 🗂️answer
                        │   │   ├── 🗂️dto
                        │   │   ├── 🗂️exception
                        │   │   ├── 🗂️model
                        │   │   ├── 🗂️repository
                        │   │   └── 🗂️service
                        │   ├── 🗂️counsel
                        │   │   ├── 🗂️controller
                        │   │   ├── 🗂️dto
                        │   │   │   ├── 🗂️request
                        │   │   │   └── 🗂️response
                        │   │   ├── 🗂️exception
                        │   │   ├── 🗂️model
                        │   │   ├── 🗂️repository
                        │   │   │   └── 🗂️querydsl
                        │   │   └── 🗂️service
                        │   ├── 🗂️like
                        │   │   ├── 🗂️repository
                        │   │   └── 🗂️service
                        │   ├── 🗂️question
                        │   │   ├── 🗂️exception
                        │   │   ├── 🗂️model
                        │   │   ├── 🗂️repository
                        │   │   └── 🗂️service
                        │   ├── 🗂️user
                        │   │   ├── 🗂️controller
                        │   │   ├── 🗂️dto
                        │   │   ├── 🗂️exeception
                        │   │   ├── 🗂️model
                        │   │   ├── 🗂️repository
                        │   │   └── 🗂️service
                        │   └── 🗂️voice
                        │       ├── 🗂️dto
                        │       │   ├── 🗂️request
                        │       │   └── 🗂️response
                        │       ├── 🗂️exception
                        │       ├── 🗂️model
                        │       ├── 🗂️repository
                        │       └── 🗂️service
                        └── 🗂️global
                            ├── 🗂️annotation
                            ├── 🗂️common
                            ├── 🗂️config
                            ├── 🗂️dto
                            │   ├── 🗂️request
                            │   └── 🗂️response
                            ├── 🗂️exception
                            │   ├── 🗂️handler
                            │   └── 🗂️servererror
                            ├── 🗂️fliter
                            ├── 🗂️repositoty
                            └── 🗂️util

```



<br/>

## 🔗 ERD
<img width="901" alt="스크린샷 2025-03-09 오후 2 03 57" src="https://github.com/user-attachments/assets/fe5fc777-8116-4d78-80c1-af45b0b9935a" />


<br/>

## 📄 API 명세서
<img width="901" alt="스크린샷 2025-03-09 오후 2 03 57" src="https://github.com/user-attachments/assets/0a12919f-4557-4f72-879f-faec538d3398" />


<br/>

## 🛠️ Tech Stack
| 사용기술               | 정보                            |
|--------------------|-------------------------------|
| Spring             | 3.2.1                         |
| Database           | AWS RDS(MySQL)                |
| Instance           | AWS EC2(Amazon Linux)               |
| CI/CD	             | Github Actions, Docke |
| Build Tool         | Gradle                        |
| ERD                | ERDCloud           |
| Java               | Java 17                       |
| Springdoc | *                         |
| Redis              | *                        |
| Jpa            | *                         |
| Querydsl           | *                         |



<br/>

## 🔨 Architecture
<img width="918" alt="스크린샷 2025-03-09 오후 2 13 56" src="https://github.com/user-attachments/assets/e5501012-95a9-436c-82be-1f556bd68834" />


```````
