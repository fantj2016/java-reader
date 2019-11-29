### 1. 创建项目

```
create-react-app xxx
```

### 2. 统一样式
安装`styled-components`...
>使css样式只对局部生效，而不是全局生效。(react默认是全局生效)

下载`reset.css`:https://meyerweb.com/eric/tools/css/reset/，并替换全局样式，将各个浏览器中样式做统一。

##### style.js 样式文件代码
```
import {createGlobalStyle} from 'styled-components'

export const Globalstyle = createGlobalStyle`
    html, body, div, span, applet, object, iframe,
    h1, h2, h3, h4, h5, h6, p, blockquote, pre,
    a, abbr, acronym, address, big, cite, code,
    del, dfn, em, img, ins, kbd, q, s, samp,
    small, strike, strong, sub, sup, tt, var,
    b, u, i, center,
    dl, dt, dd, ol, ul, li,
    fieldset, form, label, legend,
    table, caption, tbody, tfoot, thead, tr, th, td,
    article, aside, canvas, details, embed, 
    figure, figcaption, footer, header, hgroup, 
    menu, nav, output, ruby, section, summary,
    time, mark, audio, video {
        margin: 0;
        padding: 0;
        border: 0;
        font-size: 100%;
        font: inherit;
        vertical-align: baseline;
    }
    /* HTML5 display-role reset for older browsers */
    article, aside, details, figcaption, figure, 
    footer, header, hgroup, menu, nav, section {
        display: block;
    }
    body {
        line-height: 1;
    }
    ol, ul {
        list-style: none;
    }
    blockquote, q {
        quotes: none;
    }
    blockquote:before, blockquote:after,
    q:before, q:after {
        content: '';
        content: none;
    }
    table {
        border-collapse: collapse;
        border-spacing: 0;
    }
`
```

##### 引入全局样式 ： App.js
```
import React, {Component, Fragment} from 'react';
import { Globalstyle } from './style.js';
import { Icon} from "./static/iconfont/iconfont";
import Header from './common/header/index'

class App extends Component {
  render() {
    return (
        <Fragment>
            <Header/>
            <Globalstyle/>
            <Icon/>
        </Fragment>
    )
  }
}

export default App;
```
### 引入图标，并给icon的css设置成全局。

##### 1. 下载icon
并仍到/static/iconfont目录下。

##### 2. 修改css为js,并设置全局样式
```
import {createGlobalStyle} from 'styled-components'

export const IconGlobalStyle = createGlobalStyle`
@font-face {font-family: "iconfont";
  src: url('iconfont.eot?t=1541324473283'); /* IE9*/
  src: url('iconfont.eot?t=1541324473283#iefix') format('embedded-opentype'), /* IE6-IE8 */
...
  url('iconfont.ttf?t=1541324473283') format('truetype'), /* chrome, firefox, opera, Safari, Android, iOS 4.2+*/
  url('iconfont.svg?t=1541324473283#iconfont') format('svg'); /* iOS 4.1- */
}

.iconfont {
  font-family:"iconfont" !important;
  font-size:16px;
  font-style:normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.icon-weibiaoti1:before { content: "\e61b"; }

.icon-Aa:before { content: "\e636"; }

.icon-sousuo:before { content: "\e66b"; }

`;
```

##### 3. 引入并使用
`<i className={this.props.focused ? 'focused iconfont' : 'iconfont'}>&#xe66b;</i>`

###
