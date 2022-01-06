# Account Library

This project is best viewed as the SDK for the “application side” of the account-sdk (An application can be thought of as a set of cordapps). The major task is to provide a virtual vault for every cordapp application user without conflict (i.e., Bob from app1 is not the same as Bob from app2. Well, maybe they are but allowing vault sharing across applications will result in chaos).

To achieve this, an application must indicate to the underlying (modified) cordapp which application the given user belongs to by showing its NetworkIdentityPlane. Then under the hood, the (modified) cordapp will be able to correctly map the given user (via a combination of its String username and NetworkIdentityPlane identitfier) to an AccountInfo. In this way we can make sure that the mapping between application user and AccountInfo is 1-1.

![Example](./docs/plane.drawio.png)