import { Stack, StackProps } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { CloudFrontStack } from './app/cloudfront-stack';
import { FrontStack } from './app/front-stack';
import { StringParameter } from 'aws-cdk-lib/aws-ssm';
import { BucketDeployment, Source } from 'aws-cdk-lib/aws-s3-deployment';
import { CacheControl } from 'aws-cdk-lib/aws-codepipeline-actions';
import { join } from 'path';
export class WebsiteStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    const productDomainName = StringParameter.fromStringParameterName(this, 'ProductServiceDomainNameParameter', '/products/distribution/domain/name');
    const bookmarkDomainName = StringParameter.fromStringParameterName(this, 'BookmarkServiceDomainNameParameter', '/bookmarks/distribution/domain/name');

    const frontStack = new FrontStack(this, FrontStack.name, {  });
    
    const ditribution = new CloudFrontStack(this, CloudFrontStack.name, { 
      FrontSiteBucket: frontStack.Bucket,
      BookmarkServiceDomainName: bookmarkDomainName.stringValue,
      ProductServiceDomainName: productDomainName.stringValue,
     });

     new BucketDeployment(this, 'BucketDeployment', {
      sources: [ Source.asset(join(process.cwd(), '/front-app/website/build')) ],
      cacheControl: [CacheControl.fromString('max-age=1800,must-revalidate')],
      destinationBucket: frontStack.Bucket,
      prune: false,
      distribution: ditribution.Distribution,
      distributionPaths: ['/*'],
  });
  
    new BucketDeployment(this, 'RedirectionDeployment', {
        sources: [ Source.asset(join(process.cwd(), '/front-app/src/redirection-files')) ],
        metadata: {
            'location': `https://${ditribution.Distribution.distributionDomainName}/H?category=ON_SOLD`,
        },
        destinationBucket: frontStack.Bucket,
        prune: false,
    });
  }
}
