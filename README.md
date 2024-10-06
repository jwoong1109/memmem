# MemMem

## 👨‍🏫 프로젝트 소개
**MemMem**은 다양한 소모임을 관리하고 참여할 수 있는 플랫폼입니다. 아래는 각 기능에 대한 설명입니다.

## ⏲️ 개발 기간
진행 중

## 🧑‍🤝‍🧑 개발자 소개
- **전용준**: 팀장
- **이유진**: 부팀장
- **이진웅**: 컨트롤 타워
- **조승범**: 서기
- **박태주**: 자료담당
- **김유진**: 자료담당
- **김윤석**: 아이디어뱅크

## 💻 개발 환경
- **버전**: Java 17
- **IDE**: Spring Tool Suite (STS)
- **프레임워크**: SpringBoot 3.3
- **ORM**: JPA

## ⚙️ 기술 스택
- **서버**: AWS EC2
- **데이터베이스**: AWS RDS
- **WAS**: Tomcat

## 💼 협업 도구
- **아이디어 회의**: Padlet, Notion, Discord

## 1. 메인 페이지
메인 페이지는 사용자가 소모임을 쉽게 탐색할 수 있도록 구성되어 있습니다.
- **회원 수 많은 순서로 정렬**: 가장 인기가 많은 소모임부터 확인할 수 있습니다.
- **최신 등록 순서로 정렬**: 새롭게 생성된 소모임을 빠르게 찾아볼 수 있습니다.
- **모임 후기 최신순 정렬**: 모임 후기를 최신 순서로 보여주어 최신 소식을 확인할 수 있습니다.

## 2. 모임 CRUD 및 관리 기능
- **모임 생성, 조회, 수정, 삭제**(CRUD): 사용자는 자신의 소모임을 쉽게 관리할 수 있습니다.
- **이미지 업로드(AWS S3 연동)**: 모임 생성 및 수정 시, 이미지를 업로드할 수 있으며, 해당 이미지는 **AWS S3 버킷**에 저장됩니다.
- **모임 공지사항**: 각 모임에 중요한 공지사항을 게시할 수 있으며, 공지는 모임 참여자들에게 알림으로 전달됩니다.
- **모임 가입 및 탈퇴**: 원하는 모임에 쉽게 가입하고, 필요할 경우 탈퇴할 수 있습니다.

## 3. 챗봇
- **날씨 API 연동**: 사용자는 모임과 관련된 다양한 질문을 할 수 있으며, 특히 모임이 열리는 날의 날씨를 실시간으로 확인할 수 있는 기능이 포함되어 있습니다. 공공데이터포털의 날씨 API와 Geolocation API를 활용하여, 사용자의 위치를 기반으로 정확한 날씨 정보를 제공합니다.
- **시나리오형 대화**: 사용자의 질문에 따라 다양한 시나리오로 응답을 제공하는 대화형 챗봇 기능을 지원합니다.
- **Komoran을 활용한 자연어 처리**:Komoran 자연어 처리 라이브러리를 사용하여 사용자의 질문을 분석하고 적절한 응답을 제공합니다. 이를 통해 사용자의 질문 의도를 정확히 파악하고, 적합한 정보나 답변을 제공할 수 있습니다.

## 4. 모임 후기 (자유게시판)
- **모임 후기 작성 및 열람**: 모임 참여자들은 자유롭게 모임 후기를 작성하고, 다른 참여자들이 작성한 후기를 볼 수 있습니다.
- **최신 순서로 정렬**: 최신 후기를 빠르게 확인할 수 있습니다.

## 5. 유저 차단 기능
- **차단된 유저의 후기 차단**: 특정 사용자를 차단하면, 차단된 사용자가 작성한 모임 후기를 볼 수 없도록 설정할 수 있습니다.

## 6. 마이페이지
- **개인 정보 관리**: 사용자는 자신의 정보를 관리할 수 있으며, 참여한 모임과 작성한 후기를 한눈에 볼 수 있습니다.
- **알림 관리**: 모임과 관련된 공지 및 알림을 확인하고 설정할 수 있습니다.

## 7. 로그인 및 회원가입
- **일반 회원가입**: 이메일과 비밀번호를 통해 일반 회원가입을 할 수 있습니다.
- **OAuth2 소셜 로그인**: 구글, 페이스북 등 다양한 소셜 계정을 통해 간편하게 로그인할 수 있는 기능을 지원합니다.
