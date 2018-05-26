import Jama.Matrix;

import java.util.Date;

public class PCA {
	public double[][] Standardlizer(double[][] x){
		int n=x.length;		//二维矩阵的行号
		int p=x[0].length;	//二维矩阵的列号
		double[] average=new double[p];	//每一列的平均值
		double[][] result=new double[n][p];	//标准化后的向量
		double[] var=new double[p];      //方差
		//取得每一列的平均值
		for(int k=0;k<p;k++){
			double temp=0;
			for(int i=0;i<n;i++){
				temp+=x[i][k];
			}
			average[k]=temp/n;
		}
		//取得方差
		for(int k=0;k<p;k++){
			double temp=0;
			for(int i=0;i<n;i++){
				temp+=(x[i][k]-average[k])*(x[i][k]-average[k]);
			}
			var[k]=temp/(n-1);
		}
		//获得标准化的矩阵
		for(int i=0;i<n;i++){
			for(int j=0;j<p;j++){
				result[i][j] = (x[i][j] - average[j]) / Math.sqrt(var[j]);
			}
		}
		return result;
	}


	//计算样本相关系数矩阵
	public double[][] CoefficientOfAssociation(double[][] x){
		int n=x.length;		//二维矩阵的行号
		int p=x[0].length;	//二维矩阵的列号
		double[][] result=new double[p][p];//相关系数矩阵
		for(int i=0;i<p;i++){
			for(int j=0;j<p;j++){
				double temp=0;
				for(int k=0;k<n;k++){
					temp+=x[k][i]*x[k][j];
				}
				result[i][j]=temp/(n-1);
			}
		}
		return result;
	}


	//计算相关系数矩阵的特征值
	public double[][] FlagValue(double[][] x){
		//定义一个矩阵
	     Matrix A = new Matrix(x);
	      //由特征值组成的对角矩阵
	     Matrix B=A.eig().getD();
	     double[][] result=B.getArray();
	     return result;
	}


	//计算相关系数矩阵的特征向量
	public double[][] FlagVector(double[][] x){
		//定义一个矩阵
	      Matrix A = new Matrix(x);
	      //由特征向量组成的对角矩阵
	     Matrix B=A.eig().getV();
	     double[][] result=B.getArray();
	     return result;
	}


	//选取前N个主成分   (N=2)
	public int[] SelectPrincipalComponent(double[][] x){
		int Dimension=2;
		int n=x.length;		//二维矩阵的行号,列号
		double[] a = new double[n];
		int[] result = new int[n];
		int k=0;
		double temp=0;
		int m=0;
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				if(i==j){
					a[k]=x[i][j];
				}
			}
			k++;
		}
		for(int i=0;i<n;i++){
			temp=a[i];
			for(int j=0;j<n;j++){
				if(a[j]>=temp){
					temp=a[j];
					k=j;
				}
				result[m]=k;
			}
			a[k]=-1000;
			m++;
		}
		int[] end=new int[Dimension];
		System.arraycopy(result, 0, end, 0, Dimension);
		return end;
	}


	//取得主成分矩阵
	public double[][] PrincipalComponent(double[][] x,int[] y){
		int n=x.length;
		double[][] Result=new double[n][y.length];
		int k=y.length-1;
		for(int i=0;i<y.length;i++){
			for(int j=0;j<n;j++){
				Result[j][i]=x[j][y[k]];
			}
			k--;
		}
		return Result;
	}


	public Matrix analyse(double [][]data){
		Date date=new Date();
		double[][] Standard=this.Standardlizer(data);
		//System.out.println("数据标准化完毕...");
	    double[][] Assosiation=this.CoefficientOfAssociation(Standard);
	    //System.out.println("样本相关系数矩阵计算完毕...");
	    double[][] FlagValue=this.FlagValue(Assosiation);
	    //System.out.println("样本相关系数矩阵的特征值计算完毕...");
	    double[][] FlagVector=this.FlagVector(Assosiation);
	    //System.out.println("样本相关系数矩阵的特征向量计算完毕...");
	    int[] xuan=this.SelectPrincipalComponent(FlagValue);
	    //System.out.println("获取主成分前N个...");
	    double[][] result=this.PrincipalComponent(FlagVector, xuan);
	    //System.out.println("主成分矩阵生成完毕...");
	    Matrix A=new Matrix(data);
	    Matrix B=new Matrix(result);
	    Matrix C=A.times(B);
	    Date date1=new Date();
	    long diff=date1.getTime()-date.getTime();
	    System.out.println("PCA SUCCESS,TIME USE"+diff+" MS");
	    return C;
	}
}
