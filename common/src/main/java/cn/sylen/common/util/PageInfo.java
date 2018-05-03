package cn.sylen.common.util;

import java.io.Serializable;
import java.util.List;

/**
 * 对Page<E>结果进行包装
 * <p/>
 * 新增分页的多项属性，主要参考:http://bbs.csdn.net/topics/360010907
 *
 * @author liuzh/abel533/isea533
 * @version 3.3.0
 * @since 3.2.2
 * 项目地址 : http://git.oschina.net/free/Mybatis_PageHelper
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PageInfo<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    public static String INDEX = "index";

    //当前页
    private int pageNum;
    //每页的数量
    private int pageSize;
    //当前页的数量
    private int size;
    //由于startRow和endRow不常用，这里说个具体的用法
    //可以在页面中"显示startRow到endRow 共size条数据"

    //当前页面第一个元素在数据库中的行号
    private int startRow;
    //当前页面最后一个元素在数据库中的行号
    private int endRow;
    //总记录数
    private long total;
    //总页数
    private int pages;
    //结果集
    private List<T> list;

    //第一页
    private int firstPage;
    //前一页
    private int prePage;
    //下一页
    private int nextPage;
    //最后一页
    private int lastPage;

    //是否为第一页
    private boolean isFirstPage = false;
    //是否为最后一页
    private boolean isLastPage = false;
    //是否有前一页
    private boolean hasPreviousPage = false;
    //是否有下一页
    private boolean hasNextPage = false;
    //导航页码数
    private int navigatePages;
    //所有导航页号
    private int[] navigatepageNums;

    /**
     * 包装Page对象
     *
     * @param list
     */
    public PageInfo(List<T> list) {
        this(list, 8);
    }

    /**
     * 包装Page对象
     *
     * @param list          page结果
     * @param navigatePages 页码数量
     */
    public PageInfo(List<T> list, int navigatePages) {
        if (list instanceof Page) {
            Page page = (Page) list;
            this.pageNum = page.getPageNum();
            this.pageSize = page.getPageSize();

            this.total = page.getTotal();
            this.pages = page.getPages();
            this.list = page.getItems();
            this.size = page.size();
            //由于结果是>startRow的，所以实际的需要+1
            if (this.size == 0) {
                this.startRow = 0;
                this.endRow = 0;
            } else {
                this.startRow = page.getStartRow() + 1;
                //计算实际的endRow（最后一页的时候特殊）
                this.endRow = this.startRow - 1 + this.size;
            }
            this.navigatePages = navigatePages;
            //计算导航页
            calcNavigatepageNums();
            //计算前后页，第一页，最后一页
            calcPage();
            //判断页面边界
            judgePageBoudary();
        }
    }

    /**
     * 计算导航页
     */
    private void calcNavigatepageNums() {
        //当总页数小于或等于导航页码数时
        if (pages <= navigatePages) {
            navigatepageNums = new int[pages];
            for (int i = 0; i < pages; i++) {
                navigatepageNums[i] = i + 1;
            }
        } else { //当总页数大于导航页码数时
            navigatepageNums = new int[navigatePages];
            int startNum = pageNum - navigatePages / 2;
            int endNum = pageNum + navigatePages / 2;

            if (startNum < 1) {
                startNum = 1;
                //(最前navigatePages页
                for (int i = 0; i < navigatePages; i++) {
                    navigatepageNums[i] = startNum++;
                }
            } else if (endNum > pages) {
                endNum = pages;
                //最后navigatePages页
                for (int i = navigatePages - 1; i >= 0; i--) {
                    navigatepageNums[i] = endNum--;
                }
            } else {
                //所有中间页
                for (int i = 0; i < navigatePages; i++) {
                    navigatepageNums[i] = startNum++;
                }
            }
        }
    }

    /**
     * 计算前后页，第一页，最后一页
     */
    private void calcPage() {
        if (navigatepageNums != null && navigatepageNums.length > 0) {
            firstPage = navigatepageNums[0];
            lastPage = navigatepageNums[navigatepageNums.length - 1];
            if (pageNum > 1) {
                prePage = pageNum - 1;
            }
            if (pageNum < pages) {
                nextPage = pageNum + 1;
            }
        }
    }

    /**
     * 判定页面边界
     */
    private void judgePageBoudary() {
        isFirstPage = pageNum == 1;
        isLastPage = pageNum == pages;
        hasPreviousPage = pageNum > 1;
        hasNextPage = pageNum < pages;
    }


    public String replaceUrl(String url, int page) {

		if (url != null && url.indexOf("?") == -1) {
			return url + "?" + INDEX + "=" + page;
		}
		if (url != null && url.indexOf("index=") == -1) {
			return url + "&" + INDEX + "=" + page;
		}
		return url == null ? "" : url.replaceAll("index=\\d{1,}", INDEX + "="
		        + page);
	}

	/**
	 * 基于Bootstrap的分页样式 @see http://v3.bootcss.com/components/#pagination
	 * @param linkUrl
	 * @return
	 */
	public String getBsPageHtml(String linkUrl) {
		StringBuffer str = new StringBuffer("");
		str.append("<nav>");
		str.append("<ul class=\"pagination pagination-lg\">");
		if (pageNum == 1) {
			str.append("<li><a>&laquo;</a></li>");
		} else {
			str.append("<li><a href=\""
			        + this.replaceUrl(linkUrl, (pageNum - 1))
			        + "\">«</a></li>");
		}

		if (pages <= 7) {
			for (int i = 1; i <= pages; i++) {
				if (i == pageNum) {
					str.append("<li class=\"active\"><a>" + i + "</a></li>");
				} else {
					str.append("<li><a href=\"").append(
					        this.replaceUrl(linkUrl, i) + "\">" + i
					                + "</a></li>");
				}
			}
		}
		if (pages > 7) {
			if (pageNum < 6) {
				for (int i = 1; i <= 6; i++) {
					if (i == pageNum) {
						str.append("<li class=\"active\"><a href=\"").append(
						        this.replaceUrl(linkUrl, i) + "\" >" + i
						                + "</a></li>");
					} else {
						str.append("<li><a  href=\"").append(
						        this.replaceUrl(linkUrl, i) + "\" >" + i
						                + "</a></li>");
					}
				}
				str.append("<li><a>...</a></li>");
				str.append("<li><a href=\"").append(
				        this.replaceUrl(linkUrl, pages) + "\" >"
				                + pages + "</a></li>");
			} else {
				int preDiff = pageNum;
				int nextDiff = pages - pageNum;
				if (preDiff <= 4) {
					for (int i = 1; i < preDiff; i++) {
						str.append("<li><a href=\"").append(
						        this.replaceUrl(linkUrl, i) + "\" >" + i
						                + "</a></li>");
					}
				}
				if (preDiff > 4) {
					str.append("<li><a href=\"").append(
					        this.replaceUrl(linkUrl, 1) + "\" >1</a></li>");
					str.append("<li><a>...</a></li>");
					for (int i = pageNum - 2; i < pageNum; i++) {
						str.append("<li><a href=\"")
						        .append(this.replaceUrl(linkUrl, i) + "\"")
						        .append(">" + i + "</a></li>");
					}
				}
				str.append("<li class=\"active\"><a>" + pageNum
				        + "</a></li>");

				if (nextDiff <= 3) {
					for (int i = pageNum + 1; i <= pages; i++) {
						str.append("<li><a href=\"").append(
						        this.replaceUrl(linkUrl, i) + "\" >" + i
						                + "</a></li>");
					}
				}
				if (nextDiff > 3) {
					for (int i = pageNum + 1; i <= pageNum + 2; i++) {
						str.append("<li><a href=\"").append(
						        this.replaceUrl(linkUrl, i) + "\" >" + i
						                + "</a></li>");
					}
					str.append("<li><a>...</a></li>");
					str.append("<li><a  href=\"").append(
					        this.replaceUrl(linkUrl, pages) + "\" >"
					                + pages + "</a></li>");
				}
			}

		}

		if (pageNum == pages) {
			str.append("<li><a >»</a></li>");
		} else {
			str.append("<li><a href=\""
			        + this.replaceUrl(linkUrl, (pageNum + 1))
			        + "\">»</a></li>");
		}
		str.append("</ul>");
		str.append("</nav>");
		return str.toString();
	}

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getSize() {
        return size;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getEndRow() {
        return endRow;
    }

    public long getTotal() {
        return total;
    }

    public int getPages() {
        return pages;
    }

    public List<T> getList() {
        return list;
    }
    public void setList(List<T> list){
        this.list=list;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public int getPrePage() {
        return prePage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public boolean isIsFirstPage() {
        return isFirstPage;
    }

    public boolean isIsLastPage() {
        return isLastPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public int getNavigatePages() {
        return navigatePages;
    }

    public int[] getNavigatepageNums() {
        return navigatepageNums;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PageInfo{");
        sb.append("pageNum=").append(pageNum);
        sb.append(", pageSize=").append(pageSize);
        sb.append(", size=").append(size);
        sb.append(", startRow=").append(startRow);
        sb.append(", endRow=").append(endRow);
        sb.append(", total=").append(total);
        sb.append(", pages=").append(pages);
        sb.append(", list=").append(list);
        sb.append(", firstPage=").append(firstPage);
        sb.append(", prePage=").append(prePage);
        sb.append(", nextPage=").append(nextPage);
        sb.append(", lastPage=").append(lastPage);
        sb.append(", isFirstPage=").append(isFirstPage);
        sb.append(", isLastPage=").append(isLastPage);
        sb.append(", hasPreviousPage=").append(hasPreviousPage);
        sb.append(", hasNextPage=").append(hasNextPage);
        sb.append(", navigatePages=").append(navigatePages);
        sb.append(", navigatepageNums=");
        if (navigatepageNums == null) sb.append("null");
        else {
            sb.append('[');
            for (int i = 0; i < navigatepageNums.length; ++i)
                sb.append(i == 0 ? "" : ", ").append(navigatepageNums[i]);
            sb.append(']');
        }
        sb.append('}');
        return sb.toString();
    }
}
